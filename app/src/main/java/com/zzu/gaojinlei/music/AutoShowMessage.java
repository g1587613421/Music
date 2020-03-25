/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

/**
 * 封装QMUI官方的QMUITipDialog
 * 不知道QMUI官方怎么设计的每次启动关闭比安卓自带的Toast还要麻烦--这里针对我个人习惯改装一下
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/25 10:17
 * @项目名 Music
 */
public class AutoShowMessage {
    static public  int deftime=800;
    //默认-1是不带图片的
     static final int FAIL=QMUITipDialog.Builder.ICON_TYPE_FAIL;
     static final int LOADING=QMUITipDialog.Builder.ICON_TYPE_LOADING;
     static final int SUCCESS=QMUITipDialog.Builder.ICON_TYPE_SUCCESS;
     static final int NOTHING=QMUITipDialog.Builder.ICON_TYPE_NOTHING;
     //单例模式
     private AutoShowMessage(){

     }

    /**
     * 通用消息提示控制器-核心层
     * @param context--注意此处必须是要显示消息的activity的上下文,如果是getApplication的将会报错(无法定位显示位置)--我感觉官方可以改进一下--可以去栈里找到当前activity-就像Toast一样
     * @param setIconType
     * @param title
     * @param time
     */
    public static void  showQMUIMessage(Context context,int setIconType, String title,final int time){
        final QMUITipDialog tipDialog;

        if (setIconType!=-1)
            tipDialog = new QMUITipDialog.Builder(context).setTipWord(title).setIconType(setIconType).create();
        else tipDialog = new QMUITipDialog.Builder(context).setTipWord(title).create();
        //线程锁便于以后程序的扩展--暂时没用
        synchronized (tipDialog){
        tipDialog.show();
        new Thread(){
            @Override
            public void run() {

                try {
                    sleep(time);
                    tipDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        };
    }
    //省略时间
    public static void showQMUIMessage(Context context,int setIconType, String title){
        showQMUIMessage(context,setIconType,title,deftime);
    }
    //只有图片
    public static void showQMUIMessage(Context context,int setIconType){
        showQMUIMessage(context,setIconType,"");
    }
    //只有文本
    public static void showQMUIMessage(Context context,String title,int time){
        showQMUIMessage(context,-1,title,time);
    }
    //只有文本不带时间
    public static void showQMUIMessage(Context context,String title){
        showQMUIMessage(context,title,deftime);
    }

    public static AutoShowMessage getInstance() {
        return new AutoShowMessage();
    }
}
