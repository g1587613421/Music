/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music;

import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.view.View;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/26 10:57
 * @项目名 Music
 */
public class EffectManage implements OnSeekChangeListener {
    static int t1, ZQ1 = 0, ZQ2 = 0, ZQ3 = 0, ZQ4 = 0, ZQ5 = 0;
    static EffectManage effectManage;
     MediaPlayer mediaPlayer;
    static BassBoost bassBoost;
    static Equalizer equalizer;
    boolean equalizerEnable=true;
    //启动监听服务
    public static EffectManage getInstance() {
        if (effectManage==null||effectManage.mediaPlayer!=MainActivity.getMediaPlayer())
            effectManage=getInstance(MainActivity.getMediaPlayer());
        return effectManage;
    }

//后续扩展使用
    private static EffectManage getInstance( MediaPlayer mediaPlayer) {
        return new EffectManage(mediaPlayer);
    }
    private EffectManage(MediaPlayer mediaPlayer){
        if (mediaPlayer==null)
            return;
        this.mediaPlayer=mediaPlayer;
        bassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(equalizerEnable);
    }

    @Override
    public void onSeeking(SeekParams seekParams) {
        switch (seekParams.seekBar.getId()){
            case R.id.seek:
                t1=seekParams.progress;
                break;
            case R.id.ZQseek1:
                ZQ1=seekParams.progress;
                break;
            case R.id.ZQseek2:
                ZQ2=seekParams.progress;
                break;
            case R.id.ZQseek3:
                ZQ3=seekParams.progress;
                break;
            case R.id.ZQseek4:
                ZQ4=seekParams.progress;
                break;
            case R.id.ZQseek5:
                ZQ5=seekParams.progress;
                break;
        }

        update();
    }

    public void update() {
        if (!mediaPlayer.isPlaying())
            return;
        bassBoost.setEnabled(t1!=0);
        bassBoost.setStrength((short)t1);
        equalizer.setEnabled(true);
        equalizer.setBandLevel((short) 0,(short) ZQ1);
        equalizer.setBandLevel((short) 1,(short) ZQ2);
        equalizer.setBandLevel((short) 2,(short) ZQ3);
        equalizer.setBandLevel((short) 3,(short) ZQ4);
        equalizer.setBandLevel((short) 4,(short) ZQ5);
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

    }

    public void initIndicatorSeekBar(View decorView) {
        ((IndicatorSeekBar)decorView.findViewById(R.id.seek)).setProgress(t1);
        ((IndicatorSeekBar)decorView.findViewById(R.id.ZQseek1)).setProgress(ZQ1);
        ((IndicatorSeekBar)decorView.findViewById(R.id.ZQseek2)).setProgress(ZQ2);
        ((IndicatorSeekBar)decorView.findViewById(R.id.ZQseek3)).setProgress(ZQ3);
        ((IndicatorSeekBar)decorView.findViewById(R.id.ZQseek4)).setProgress(ZQ4);
        ((IndicatorSeekBar)decorView.findViewById(R.id.ZQseek5)).setProgress(ZQ5);
    }
    public void reset(){
        ZQ1=ZQ2=ZQ3=ZQ4=ZQ5=t1=0;
        update();
    }
}
