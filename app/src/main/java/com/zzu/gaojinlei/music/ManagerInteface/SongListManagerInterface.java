/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.ManagerInteface;

import com.zzu.gaojinlei.music.Data.MusicData;

import java.util.LinkedList;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/6 22:56
 * @项目名 Music
 */
public interface SongListManagerInterface {
    void initList(LinkedList<MusicData> linkedList, boolean isplaying);
}
