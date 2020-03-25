package com.zzu.gaojinlei.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.link.QMUIScrollingMovementMethod;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.zzu.gaojinlei.music.View.LrcView;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    LrcView lrcView;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    MediaPlayer mediaPlayer;
    CoverManage coverManage;//封面管理器
    LinkedList<MusicData> musicsData=new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化数据
        musicsData.add(new MusicData(R.raw.a11,R.string.盛夏,R.drawable.music_default_bg));
        //初始化音乐环境
        initializeTheEnvironment();





    }

    private void initlCoverManage() {
        coverManage = CoverManage.builder();
        coverManage.setCover((QMUIRadiusImageView) findViewById(R.id.QMUIRadiusImageView5),getApplicationContext());
    }

    private void initializeTheEnvironment() {
        mediaPlayer = new MediaPlayer();
        //初始化歌词控件
        initlrc("暂时没有找到歌词");
//        initlrc(getResources().getString(R.string.盛夏));
        //初始化封面管理器
        initlCoverManage();
        try {
            mediaPlayer.setDataSource(getResources().openRawResourceFd((int) musicsData.peek().song));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lrcView.setLrcString(getResources().getString(musicsData.peek().lrc));
        coverManage.setImage(musicsData.peek().coverImage);
    }

    private LrcView initlrc(String lrcSources){
        lrcView=findViewById(R.id.lv);
        lrcView.setLrcString(lrcSources);
        //权限检查
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
        return lrcView;
    }

    public void startOrpause(View view) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new PreparedListener());
            coverManage.start();
            Toast.makeText(this, "开始播放", Toast.LENGTH_SHORT).show();
            ((QMUIRadiusImageView) findViewById(R.id.control)).setImageResource(R.drawable.tingzhi);
            ((QMUIRadiusImageView) findViewById(R.id.control)).setColorFilter(Color.RED);
        } else {
            mediaPlayer.stop();
            coverManage.pause();
            ((QMUIRadiusImageView) findViewById(R.id.control)).setImageResource(R.drawable.bofang);
            ((QMUIRadiusImageView) findViewById(R.id.control)).setColorFilter(Color.GREEN);
        }
    }
    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            mediaPlayer.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 当歌曲还在播放时
                    // 就一直调用changeCurrent方法
                    // 虽然一直调用， 但界面不会一直刷新
                    // 只有当唱到下一句时才刷新
                    while(mediaPlayer.isPlaying()) {
                        // 调用changeCurrent方法， 参数是当前播放的位置
                        // LrcView会自动判断需不需要下一行
                        lrcView.changeCurrent(mediaPlayer.getCurrentPosition());

                        // 当然这里还是要睡一会的啦
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    /**
     * 权限回调监听
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==0x001)
            Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }


}
