/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.Notify;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.zzu.gaojinlei.music.Data.MusicData;
import com.zzu.gaojinlei.music.MainActivity;
import com.zzu.gaojinlei.music.R;
import com.zzu.gaojinlei.music.Tools.AutoShowMessage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/7 20:33
 * @项目名 Music
 */
public class NotifyManager{


    public static boolean sendMessage(Context context, MusicData musicData){
        if (musicData==null)
            musicData=new MusicData(R.drawable.juhao,R.string.句号,R.drawable.juhao);
        //检查权限
        if (!isNotificationEnabled(context))
        {
            AutoShowMessage.showQMUIMessage(context,AutoShowMessage.FAIL,"没有通知权限请,通知栏无法正常显示");
            return false;
        }
        RemoteViews contentView=new RemoteViews(context.getPackageName(),R.layout.notify_layout);

        contentView.setTextViewText(R.id.not_songname,musicData.getName());
        contentView.setTextViewText(R.id.not_singer,musicData.getSinger());
        contentView.setImageViewBitmap(R.id.imageView, BitmapFactory.decodeResource(context.getResources(),musicData.getCoverImage()));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( context,0,intent,0);

        NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 26)
        {
            //当sdk版本大于26
            String id = "gjl";
            String description = "gjlMessage";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
//                     channel.enableLights(true);
//                     channel.enableVibration(true);//
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, id)
                    //优先级.
                             .setAutoCancel(true)
                             .setOngoing(false)
                             .setCustomContentView(contentView)                       //完全自定义,不调用.setStyle()
                             .setContent(contentView)                                 //兼容4.1及以下
                             .setOnlyAlertOnce(true)
                            .setSmallIcon(R.drawable.bofang)
                             .build();
            manager.notify(1, notification);
        }
        else
        {
            //当sdk版本小于26
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle("无法正常显示通知")
                    .setContentText("系统版本太低,请联系开发者")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            manager.notify(1,notification);
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;

        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
