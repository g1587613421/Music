/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.Data;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/25 11:46
 * @项目名 Music
 */
public class MusicData implements MusicDataInterface {
   private int song;
    private int lrc;
    private int coverImage;
    private String name="未知";
    private String singer="未知";

    public MusicData(int song, int lrc, int coverImage){
        this.coverImage=coverImage;
        this.song=song;
        this.lrc=lrc;
    }
   public MusicData(int song,int lrc,int coverImage,String name,String singer){
        this(song,lrc,coverImage);
        this.singer=singer;
        this.name=name;
    }

    @Override
    public int getSong() {
        return song;
    }

    @Override
    public void setSong(int song) {
        this.song = song;
    }

    @Override
    public int getLrc() {
        return lrc;
    }

    @Override
    public void setLrc(int lrc) {
        this.lrc = lrc;
    }

    @Override
    public int getCoverImage() {
        return coverImage;
    }

    @Override
    public void setCoverImage(int coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSinger() {
        return singer;
    }

    @Override
    public void setSinger(String singer) {
        this.singer = singer;
    }
}