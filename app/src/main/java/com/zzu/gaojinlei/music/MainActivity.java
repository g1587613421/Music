package com.zzu.gaojinlei.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.link.QMUIScrollingMovementMethod;
import com.zzu.gaojinlei.music.View.LrcView;

public class MainActivity extends AppCompatActivity {
LrcView lrcView;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ((TextView)findViewById(R.id.lv)).setMovementMethod(new QMUIScrollingMovementMethod());
        MediaPlayer mediaPlayer=new MediaPlayer();
        lrcView=findViewById(R.id.lv);
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        init();
    }
    private void init(){
        lrcView.setLrcString(getResources().getString(R.string.盛夏));
//        AssetManager assetManager=getAssets();
//        assetManager.open()
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==0x001)
            Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }
}
