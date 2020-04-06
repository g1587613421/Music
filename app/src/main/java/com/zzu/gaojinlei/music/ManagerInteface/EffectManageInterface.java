/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.ManagerInteface;

import android.view.View;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.SeekParams;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/6 22:55
 * @项目名 Music
 */
public interface EffectManageInterface {
    void onSeeking(SeekParams seekParams);

    void update();

    void onStartTrackingTouch(IndicatorSeekBar seekBar);

    void onStopTrackingTouch(IndicatorSeekBar seekBar);

    void initIndicatorSeekBar(View decorView);

    void reset();
}
