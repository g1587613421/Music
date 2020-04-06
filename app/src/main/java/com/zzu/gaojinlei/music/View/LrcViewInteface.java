/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.View;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEventSource;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/7 7:06
 * @项目名 Music
 */
interface LrcViewInteface extends Drawable.Callback, KeyEvent.Callback, AccessibilityEventSource {
    void changeProcess();

    // 传入当前播放时间
    void changeCurrent(long time);

    /**加载歌词文本内容
     *
     * @param data
     */
    void setLrcString(String data);

    // 设置lrc的路径
    void setLrcPath(String path) throws Exception;

    // 外部提供方法
    // 设置背景图片
    void setBackground(Bitmap bmp);

    void setRefreshTime(int refreshTime);

    int getmLrcHeight();

    void setmLrcHeight(int mLrcHeight);
    //最小显示行数
    int getmRows();

    void setmRows(int mRows);

    void setmTextSize(float mTextSize);

    void setmDividerHeight(float mDividerHeight);
}
