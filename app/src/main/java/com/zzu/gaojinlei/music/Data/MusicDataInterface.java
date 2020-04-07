/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.Data;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/6 22:48
 * @项目名 Music
 */
interface MusicDataInterface {
    int song = -1;
     int lrc = -1;
     int coverImage=-1;
     String name="未知";
     String singer="未知";
    int getSong();

    void setSong(int song);

    int getLrc();

    void setLrc(int lrc);

    int getCoverImage();

    void setCoverImage(int coverImage);

    String getName();

    void setName(String name);

    String getSinger();

    void setSinger(String singer);
}
