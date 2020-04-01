package com.zzu.gaojinlei.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.DragStartHelper;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.qmuiteam.qmui.alpha.QMUIAlphaTextView;
import com.qmuiteam.qmui.layout.QMUIButton;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.popup.QMUIFullScreenPopup;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zzu.gaojinlei.music.View.LrcView;
import com.zzu.gaojinlei.music.View.MyVisualizerView;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    LrcView lrcView;
    Handler handler=new Handler();
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    //申请录音权限
    private  final int GET_RECODE_AUDIO = 2;
    private static String[] PERMISSION_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
   static MediaPlayer mediaPlayer;
   public static MyVisualizerView myVisualizerView;
    CoverManage coverManage;//封面管理器
   static private LinkedList<MusicData> musicsData=new LinkedList<>();
    boolean ispause=false;
    Thread lrcContal;
    IndicatorSeekBar timeSeekBar;
    //保证进度条滑动调节更流畅
    boolean onSeekBar=false;
    QMUIFullScreenPopup lrcFullSceenPopup;
    LrcView fullLrcview;
    //降低全屏歌词的后台资源消耗
    boolean onshow=false;
    //歌词颜色控制
    boolean colorRan=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initPermission
        initPermission();
        //初始化数据
        musicsData.addLast(new MusicData(R.raw.shaonian,R.string.少年,R.drawable.shaonian,"少年","梦然"));
        musicsData.add(new MusicData(R.raw.a11,R.string.盛夏,R.drawable.music_default_bg,"盛夏","毛不易"));
        musicsData.addLast(new MusicData(R.raw.juhao,R.string.句号,R.drawable.juhao,"句号","邓紫棋"));
        //初始化音乐环境
        initializeTheEnvironment();
        //初始化控件
        initMusicView();
        //侧边栏
        initDrawerLayout();

        myVisualizerView=findViewById(R.id.myVisualizer);
    }

    private void initPermission() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }
    }

    boolean isOpenDrawer=false;
    private void initDrawerLayout() {

        //启动组件监听服务
      new Thread(){
            @Override
            public void run() {
                while (true) {
                    while (!isOpenDrawer){
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (SongListManager.change) {
                        SongListManager.change = false;
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initializeTheEnvironment();
                                    startOrpause(null);
                                    if (SongListManager.getInstance()!=null)
                                        SongListManager.getInstance().initList(musicsData,mediaPlayer.isPlaying());
                                }
                            });

                        }
                    }
                }
            }
        }.start();
        SongListManager.getInstance(this, (QMUIGroupListView) findViewById(R.id.listView));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);/*重点，获取主界面的布局，因为没有这句话我才报错*/

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                SongListManager.getInstance().initList(musicsData,mediaPlayer.isPlaying());
                isOpenDrawer=true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isOpenDrawer=false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
//8.0以上不适用
//        drawerLayout.setFocusableInTouchMode(true);
//        drawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                SongListManager.getInstance().initList(musicsData,mediaPlayer.isPlaying());
//                if (hasFocus)
//                    listrner.start();
//                else
//                    listrner.stop();
//            }
//        });


    }

    private void initMusicView() {
        timeSeekBar=findViewById(R.id.timeSeek);
        timeSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onSeekBar=true;
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                //减少出现修改失败的概率
                float middle=timeSeekBar.getProgressFloat();
                onSeekBar=false;
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo((int)(middle*mediaPlayer.getDuration()));
                    lrcView.changeProcess();
                    fullLrcview.changeProcess();
                }
                else {
                    AutoShowMessage.showQMUIMessage(MainActivity.this,AutoShowMessage.FAIL,"请先播放音乐",1000);
                }
            }
        });



        lrcFullSceenPopup=new QMUIFullScreenPopup(this);
        QMUILinearLayout linearLayout= (QMUILinearLayout) QMUILinearLayout.inflate(this,R.layout.fullsceen_lrc,null);
        lrcFullSceenPopup.addView(linearLayout);
        fullLrcview=linearLayout.findViewById(R.id.lrcView);
        lrcFullSceenPopup.closeBtn(false);
    }

    private void initlCoverManage() {
        coverManage = CoverManage.builder();
        coverManage.setCover((QMUIRadiusImageView) findViewById(R.id.QMUIRadiusImageView5),getApplicationContext());
    }

    private void initializeTheEnvironment() {
        mediaPlayer = new MediaPlayer();
//        initlrc(getResources().getString(R.string.盛夏));
        //初始化封面管理器
        initlCoverManage();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaPlayer.setDataSource(getResources().openRawResourceFd((int) musicsData.peek().song));
            }
        } catch (IOException e) {
           AutoShowMessage.showQMUIMessage(this,"手机版本过低无法播放",AutoShowMessage.FAIL);
        }

        mediaPlayer.setOnPreparedListener(new PreparedListener());
        mediaPlayer.setOnCompletionListener(new CompletionListener());
        ((QMUIAlphaTextView)findViewById(R.id.songname)).setText(musicsData.peek().name);
        ((QMUIAlphaTextView)findViewById(R.id.player)).setText((musicsData.peek().singer));
//初始化歌词控件
        initlrc(getResources().getString(musicsData.peek().lrc).equals("")?"暂时没有找到歌词":getResources().getString(musicsData.peek().lrc));
        coverManage.setImage(musicsData.peek().coverImage);



    }

    private LrcView initlrc(String lrcSources){
        lrcView=findViewById(R.id.lv);
        lrcView.setLrcString(lrcSources);
        lrcView.changeCurrent(0L);
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
            if (ispause){
                mediaPlayer.start();
                ispause=false;
            }else {
                try {
                    mediaPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    initializeTheEnvironment();
                    startOrpause(null);
                }
            }
            coverManage.start();
            Toast.makeText(this, "开始播放", Toast.LENGTH_SHORT).show();
            ((QMUIRadiusImageView) findViewById(R.id.control)).setImageResource(R.drawable.tingzhi);
            ((QMUIRadiusImageView) findViewById(R.id.control)).setColorFilter(Color.RED);
        } else {
            mediaPlayer.pause();
            coverManage.pause();
            ispause=true;
            ((QMUIRadiusImageView) findViewById(R.id.control)).setImageResource(R.drawable.bofang);
            ((QMUIRadiusImageView) findViewById(R.id.control)).setColorFilter(Color.GREEN);
        }

    }

    public void nextMusic(View view) {
        mediaPlayer.release();
        if (lrcContal!=null&&!lrcContal.isInterrupted())
        lrcContal.interrupt();
        musicsData.addFirst(musicsData.pollLast());
        timeSeekBar.setProgress(0);
        initializeTheEnvironment();
        startOrpause(null);
    }

    public void lastMusic(View view) {
        mediaPlayer.release();
        musicsData.addLast(musicsData.pollFirst());
        timeSeekBar.setProgress(0);
        initializeTheEnvironment();
        startOrpause(null);
    }

    public void showFullSceenLrc(final View view)
    {
       final QMUIPopup qmuiPopup= QMUIPopups.popup(this, QMUIDisplayHelper.dp2px(this, 250))
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .view(R.layout.pop_s)
                .edgeProtection(QMUIDisplayHelper.dp2px(this, 20))
                .offsetX(QMUIDisplayHelper.dp2px(this, 20))
                .offsetYIfBottom(QMUIDisplayHelper.dp2px(this, 5))
                .shadow(true)
                .arrow(true)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .show(view);
        ((Switch)(qmuiPopup.getDecorView().findViewById(R.id.changeColor))).setChecked(colorRan);

       //自动关闭
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    sleep(3000);
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            qmuiPopup.dismiss();
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

    public void toshowfullsceen(final View view) {
        AutoShowMessage.showQMUIMessage(MainActivity.this,AutoShowMessage.LOADING,"注意全屏歌词,可能会造成卡顿",1000);
        lrcFullSceenPopup.onDismiss(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                onshow=false;
            }
        });
        fullLrcview.setmRows(40);
        fullLrcview.setmTextSize(60);
        fullLrcview.setmDividerHeight(20f);
        onshow=true;
        fullLrcview.setLrcString(getResources().getString(musicsData.peek().lrc));
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.fullscreenbg);
//        fullLrcview.setBackground(bitmap);
        //此处等待资源加载完毕,不让将处于卡顿状态
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        lrcFullSceenPopup.show(findViewById(R.id.timeSeek));
                    }
                });

            }
        }.start();


    }

    public void changeEffect(View view) {
       final QMUIPopup popup= QMUIPopups.popup(this, QMUIDisplayHelper.dp2px(this, 250))
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .view(R.layout.effect_change)
                .edgeProtection(QMUIDisplayHelper.dp2px(this, 20))
                .offsetX(QMUIDisplayHelper.dp2px(this, 20))
                .offsetYIfBottom(QMUIDisplayHelper.dp2px(this, 5))
               .radius(50)
                .shadow(true)
                .arrow(true)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
               .onDismiss(new PopupWindow.OnDismissListener() {
                   @Override
                   public void onDismiss() {
                       AutoShowMessage.showQMUIMessage(MainActivity.this,AutoShowMessage.SUCCESS,"将使用此声音效果");
                   }
               })
                .show(view);
       final EffectManage effectManage=EffectManage.getInstance();
        ((IndicatorSeekBar)popup.getDecorView().findViewById(R.id.seek)).setOnSeekChangeListener(effectManage);
        ((IndicatorSeekBar)popup.getDecorView().findViewById(R.id.ZQseek1)).setOnSeekChangeListener(effectManage);
        ((IndicatorSeekBar)popup.getDecorView().findViewById(R.id.ZQseek2)).setOnSeekChangeListener(effectManage);
        ((IndicatorSeekBar)popup.getDecorView().findViewById(R.id.ZQseek3)).setOnSeekChangeListener(effectManage);
        ((IndicatorSeekBar)popup.getDecorView().findViewById(R.id.ZQseek4)).setOnSeekChangeListener(effectManage);
        ((IndicatorSeekBar)popup.getDecorView().findViewById(R.id.ZQseek5)).setOnSeekChangeListener(effectManage);
        effectManage.initIndicatorSeekBar(popup.getDecorView());
        ((QMUIAlphaTextView)popup.getDecorView().findViewById(R.id.resetEffect)).setOnClickListener(new QMUIAlphaTextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog qmuiDialog = new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                        .setTitle("确定要重置吗?")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                {
                                    effectManage.reset();
                                    effectManage.initIndicatorSeekBar(popup.getDecorView());
                                    final QMUITipDialog qmuiTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).create();
                                    qmuiTipDialog.show();
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            qmuiTipDialog.cancel();
//                        qmuiFullScreenPopup.show(linearLayout);
                                        }
                                    }.start();
                                }
                                dialog.dismiss();
                            }
                        })
                        .create();
                qmuiDialog.show();
            }
        });



    }

    public void changeColor(View view) {
        if (((Switch)view).isChecked()){
            colorRan=true;
            lrcView.randomColor=true;
        }else {
            colorRan=false;
            lrcView.randomColor=false;
        }
    }

    private class CompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            mediaPlayer.release();
            nextMusic(null);
        }
    }
    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            VisualizerManage.getInstance(myVisualizerView);
            SongListManager.getInstance().initList(musicsData,true);
           lrcContal=new LrcControl();
           lrcContal.start();
            mediaPlayer.start();
            //绑定音频到音效控制器
            EffectManage.getInstance().update();
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 权限回调监听
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==0x001){
            //            Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
        }
    }

class LrcControl extends Thread{
    @Override
    public void run() {
        //不影响歌词的情况下尽可能减少SeekBar的调节冲突
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        if (!onSeekBar) {
                            timeSeekBar.setProgress((float) ( mediaPlayer.getCurrentPosition() / (mediaPlayer.getDuration()+0.01)));
                        }
                        if (mediaPlayer.isPlaying()&&onshow) {
                            fullLrcview.changeCurrent(mediaPlayer.getCurrentPosition());
                            fullLrcview.randomColor=colorRan;
                        }
                        sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        }.start();
        while(true) {
            // 调用changeCurrent方法， 参数是当前播放的位置
            try {
                //在歌曲切换的时候有极低概率恰好mediaPlayer尚未初始化!!!!!
                if (mediaPlayer.isPlaying()){
                    lrcView.changeCurrent(mediaPlayer.getCurrentPosition());
                }
                Thread.sleep(300);
            } catch (Exception e) {
            }
        }
    }
}


}
