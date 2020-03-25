package com.zzu.gaojinlei.music;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/25 11:46
 * @项目名 Music
 */
public class MusicData {
    int song;
    int lrc;
    int coverImage;
    MusicData(int song,int lrc,int coverImage){
        this.coverImage=coverImage;
        this.song=song;
        this.lrc=lrc;
    }
}
