package com.zzu.gaojinlei.music;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/25 10:44
 * @项目名 Music
 */
public class CoverManage {
   static QMUIRadiusImageView cover;
  static CoverManage coverManage;
//  static Thread thread;
  Context context;
    Animation mAnimation;
  long ra=0;
    //实现单例模式防止数据紊乱
    private CoverManage(){
        //下面的这种消耗cpu过大--弃用
//        thread=new Thread(){
//            @Override
//            public void run() {
//                while (true){
//                    new Handler(context.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
//                            ra+=2;
//                            cover.animate().setDuration(100);
//                            cover.animate().rotation(100);
//                            cover.animate().start();
//                        }
//                    });
//
//
//                try {
//                    sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }}
//        };
    }
    public static CoverManage builder(){
        return coverManage==null?new CoverManage():coverManage;
    }

    public  void setCover(QMUIRadiusImageView cover,Context context) {
        this.context=context;
        CoverManage.cover = cover;
    }
    public void setImage(int image){
        cover.setImageResource(image);
    }
    public  QMUIRadiusImageView getCover() {
        return cover;
    }
public  void start(){
    LinearInterpolator lir = new LinearInterpolator();
    mAnimation = AnimationUtils.loadAnimation(context, R.anim.coveranim);
    mAnimation.setInterpolator(lir);
    cover.startAnimation(mAnimation);
//    thread.start();
}
    public void pause(){
//        thread.interrupt();
//        cover.setAnimation(null);
        cover.clearAnimation();
    }
}
