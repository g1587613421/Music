/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

/**适配器类
 * 封装QMUI官方的QMUITipDialog(适配器模式)
 * 不知道QMUI官方怎么设计的每次启动关闭比安卓自带的Toast还要麻烦--这里针对我个人习惯改装一下
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/25 10:17
 * @项目名 Music
 */
public class AutoShowMessage {
   private volatile static Context scontext;
   //防止指令重排
    private volatile static AutoShowMessage autoShowMessage;
    private volatile static QMUITipDialog tipDialog;
    static private   int deftime=800;
    //默认-1是不带图片的
     static final int FAIL=QMUITipDialog.Builder.ICON_TYPE_FAIL;
     static final int LOADING=QMUITipDialog.Builder.ICON_TYPE_LOADING;
     static final int SUCCESS=QMUITipDialog.Builder.ICON_TYPE_SUCCESS;
     static final int NOTHING=QMUITipDialog.Builder.ICON_TYPE_NOTHING;
     //单例模式--con一次启动永远任意使用
     private AutoShowMessage(Context con){
         scontext=con;
     }

    /**
     * 单例懒汉模式
     * @param con 当前上下文
     * @return autoShowMessage
     */
     public  static AutoShowMessage getInstance(Context con) {
         if (con!=null&&con != scontext) {
             scontext=con;
         }
        if (autoShowMessage==null)
        {
            //保证多线程安全--volatile防止多线程指令重排问题
            synchronized (autoShowMessage){
                //再次确认--防止多线程下内存溢出
                if (autoShowMessage==null)
                    autoShowMessage=new AutoShowMessage(con);
            }
        }
        return autoShowMessage;
    }
    /**充当建造者
     * 通用消息提示控制器-核心层
     * contex-不强制但是有可能出现下述错误!!!!
     * @param context--注意此处必须是要显示消息的activity的上下文,如果是getApplication或者不是当前主线程的将会报错(无法定位显示位置)--我感觉官方可以改进一下--可以去栈里找到当前activity-就像Toast一样
     * @param setIconType
     * @param title
     * @param time
     */
    public static void  showQMUIMessage(Context context,int setIconType, String title,final int time){
        if (context==null)//兼容如果取不到contex的是否默认使用初始化位置的---不推荐使用
            context=scontext;
        //防止提示堆叠
        synchronized (tipDialog){
        if (setIconType!=-1)
            tipDialog = new QMUITipDialog.Builder(context).setTipWord(title).setIconType(setIconType).create();
        else tipDialog = new QMUITipDialog.Builder(context).setTipWord(title).create();

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



    public static int getDeftime() {
        return deftime;
    }

    public static void setDeftime(int deftime) {
        AutoShowMessage.deftime = deftime;
    }
}
