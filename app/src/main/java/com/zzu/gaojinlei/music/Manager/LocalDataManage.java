/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.Manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/10 10:15
 * @项目名 Music
 */
public class LocalDataManage {
    SharedPreferences sharedPreferences;
    private static LocalDataManage localDataManage;
    private static SharedPreferences.Editor editor;
    public static LocalDataManage getInstance(Context context) {
        if (localDataManage==null) {
            localDataManage = new LocalDataManage();
            localDataManage.sharedPreferences=context.getSharedPreferences("gjl",Context.MODE_PRIVATE);
            localDataManage.editor=localDataManage.sharedPreferences.edit();
        }
        return localDataManage;
    }

    public boolean store(String key,int value){
        editor.putInt(key, value);


        editor.apply();
        return editor.commit();
    }
    public boolean store(String key,boolean value){
        editor.putBoolean(key, value);


        editor.apply();
        return editor.commit();
    }
    public int getdata(String key,int defvalue){

        return sharedPreferences.getInt(key, defvalue);
    }
    public boolean getdata(String key,boolean defvalue){

        return sharedPreferences.getBoolean(key, defvalue);
    }


}
