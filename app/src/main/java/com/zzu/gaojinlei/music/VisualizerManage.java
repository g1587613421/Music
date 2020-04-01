/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;

import androidx.core.app.ActivityCompat;

import com.zzu.gaojinlei.music.View.MyVisualizerView;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/27 6:09
 * @项目名 Music
 */
public class VisualizerManage {
    static VisualizerManage visualizerManage;
    // 创建MyVisualizerView组件，用于显示波形图
   static MyVisualizerView mVisualizerView;
    // 相当于设置Visualizer负责显示该MediaPlayer的音频数据
    static Visualizer mVisualizer;
    static int SessionID=0;
    public static VisualizerManage getInstance(MyVisualizerView mVisualizerView) {
        if (MainActivity.getMediaPlayer().getAudioSessionId()==SessionID)
        return visualizerManage;
        else {
            SessionID=MainActivity.getMediaPlayer().getAudioSessionId();
            visualizerManage=new VisualizerManage(SessionID,mVisualizerView);
            visualizerManage.setupVisualizer();
        }
        return visualizerManage;
    }
    private VisualizerManage(int sessionID, MyVisualizerView VisualizerView){
        if (mVisualizer!=null)
        mVisualizer.release();
        mVisualizer=new Visualizer(sessionID);
        mVisualizerView=VisualizerView;
    }





    private void setupVisualizer()
    {
        //设置需要转换的音乐内容长度，专业的说这就是采样，该采样值一般为2的指数倍，如64,128,256,512,1024。
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 为mVisualizer设置监听器
        /*
         * Visualizer.setDataCaptureListener(OnDataCaptureListener listener, int rate, boolean waveform, boolean fft
         *
         *      listener，表监听函数，匿名内部类实现该接口，该接口需要实现两个函数
                rate， 表示采样的周期，即隔多久采样一次，联系前文就是隔多久采样128个数据
                iswave，是波形信号
                isfft，是FFT信号，表示是获取波形信号还是频域信号

         */

        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener()
                {
                    //这个回调应该采集的是快速傅里叶变换有关的数据
                    @Override
                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] fft, int samplingRate)
                    {
                    }
                    //这个回调应该采集的是波形数据
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] waveform, int samplingRate)
                    {
                        // 用waveform波形数据更新mVisualizerView组件
                        mVisualizerView.updateVisualizer(waveform);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }
}
