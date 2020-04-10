/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.windows_float;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.zzu.gaojinlei.music.Launcher.LaunchActivity;
import com.zzu.gaojinlei.music.MainActivity;
import com.zzu.gaojinlei.music.R;
import com.zzu.gaojinlei.music.Tools.AutoShowMessage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/9 21:06
 * @项目名 Music
 */
public class WindowsFloat extends Service{
static boolean isRuning;

    public static void showCheck(Context context) {
        if (!checkFloatPermission(context))
            requestPermission(context);
    }


    public static void close(Context context){
        manager.removeView(mFloatLayout);
    }

    private static void requestPermission(Context context) {
        Toast.makeText(context, "点击应用名称并授予权限", Toast.LENGTH_SHORT).show();
        String androidSDK = Build.VERSION.SDK;
        if(Integer.parseInt(androidSDK)>=23&&!Settings.canDrawOverlays(context)){
            Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            MainActivity.mainActivity.startActivityForResult(intent2,1);
        }
    }
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRuning=true;
        Log.i("gjl","启动桌面歌词服务");
        showCheck(this);
        try {
            showFloatingWindow();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "注意请给予悬浮窗权限", Toast.LENGTH_LONG).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    static WindowManager manager;


    static WindowManager.LayoutParams params;
    static LinearLayout mFloatLayout;
    private void showFloatingWindow() {
        // 从布局文件，生成悬浮窗
        LayoutInflater inflater = LayoutInflater.from(getApplication());

        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.windows_layout,null);

        // 添加悬浮窗至系统服务
        params = getParams();
        manager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        mFloatLayout.setOnTouchListener(touchListener);
        manager.addView(mFloatLayout, params);
        // 浮动窗口按钮

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    }
    public static void refresh(String lrc){
        if (!isRuning)
            return;
        if (mFloatLayout!=null){
            ((TextView)mFloatLayout.findViewById(R.id.wfLrc)).setText(lrc);
         touchListener.onTouch(null,null);
        }

    }

    // 拖动浮标时修改浮标位置
   static View.OnTouchListener touchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event!=null) {
                params.x = (int) event.getRawX();
                params.y = (int) event.getRawY();
            }
            manager.updateViewLayout(mFloatLayout, params);

            return false;  // 此处必须返回false，否则OnClickListener获取不到监听
        }
    };


    private WindowManager.LayoutParams getParams()
    {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        // 设置窗体显示类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.format = PixelFormat.RGBA_8888;   			// 设置图片格式，效果为背景透明
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 	// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;		// 调整悬浮窗显示的停靠位置为左侧置顶
//
//        // 以屏幕左上角为原点，设置x、y初始值（10,10），相对于gravity
        wmParams.x = 10;
        wmParams.y = 100;
//
//        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return wmParams;
    }

    @Override
    public boolean stopService(Intent name) {
        isRuning=false;
        return super.stopService(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

