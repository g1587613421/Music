/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music.Notify;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Switch;

import com.zzu.gaojinlei.music.MainActivity;
import com.zzu.gaojinlei.music.R;
import com.zzu.gaojinlei.music.Tools.AutoShowMessage;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/4/8 10:27
 * @项目名 Music
 */
public class BoardCastManager extends BroadcastReceiver {
    static volatile BoardCastManager boardCastManager;
    final static String PREVIOUS_MUSIC="1";
    final static String START_MUSIC="2";
    final static String NEXT_MUSIC="3";
    private volatile static Object lock=new Object();
    public  static BoardCastManager getInstance() {
        if (boardCastManager==null){
            synchronized(lock){
                if (boardCastManager==null){
                    boardCastManager=new BoardCastManager();
                }
            }
        }
        return boardCastManager;
    }

   public boolean startReceive(Context context){
        //动态注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zzu.gaojinlei.music.boardCast");//这里面的Action可以根据你的包来,这里的包是com.example.xx
        context.registerReceiver(boardCastManager, intentFilter);//如果是在fragment，那么getActivity().registerReceiver(msgReceiver, intentFilter);
       System.err.println("启动广播");
        return true;
    }
    public boolean destoryReceive(Context context){
        if (boardCastManager==null)
            return false;
          context.unregisterReceiver(boardCastManager);
        return true;
    }

    long time=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (time>System.currentTimeMillis()-500)//屏蔽广播
            return;
        time=System.currentTimeMillis();
        if (intent!=null)//耦合度太高---有时间再优化吧
        switch (intent.getAction()){
            case PREVIOUS_MUSIC:
                MainActivity.mainActivity.nextMusic(null);
                break;
            case START_MUSIC:
                MainActivity.mainActivity.startOrpause(null);
                break;
            case NEXT_MUSIC:
                MainActivity.mainActivity.lastMusic(null);
        }
    }
}
