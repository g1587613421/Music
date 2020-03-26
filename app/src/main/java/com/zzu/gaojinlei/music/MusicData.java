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
    String name="未知";
    String singer="未知";

    MusicData(int song,int lrc,int coverImage){
        this.coverImage=coverImage;
        this.song=song;
        this.lrc=lrc;
    }
    MusicData(int song,int lrc,int coverImage,String name,String singer){
        this(song,lrc,coverImage);
        this.singer=singer;
        this.name=name;

    }
}
