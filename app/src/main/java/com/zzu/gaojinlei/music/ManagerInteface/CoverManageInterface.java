/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.ManagerInteface;

import android.content.Context;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/6 22:52
 * @项目名 Music
 */
public interface CoverManageInterface {
    com.zzu.gaojinlei.music.Manager.CoverManage setCover(QMUIRadiusImageView cover, Context context);

    com.zzu.gaojinlei.music.Manager.CoverManage setImage(int image);

    QMUIRadiusImageView getCover();

    void start();

    void pause();
}
