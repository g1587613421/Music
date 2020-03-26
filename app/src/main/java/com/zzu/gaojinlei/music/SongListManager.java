/*
 * Copyright (c) 2020. 高金磊编写
 */

package com.zzu.gaojinlei.music;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.LinkedList;

/**
 * @author 高金磊
 * @version 1.0
 * @date 2020/3/26 20:32
 * @项目名 Music
 */
public class SongListManager {
    public static boolean change=false;
    private static SongListManager songListManager;
    private QMUIGroupListView mGroupListView;
    private Context context;
    //单例模式---保证数据不紊乱--仅为MainActivity使用
    public static SongListManager getInstance(Context context,QMUIGroupListView mGroupListView) {
        if (songListManager==null){
           songListManager= new SongListManager(context,mGroupListView);
        }
        return  songListManager;
    }

    public static SongListManager getInstance() {
        if (songListManager==null)
            try {
                throw new Exception("请先调用(Context context,QMUIGroupListView mGroupListView)来确保正确实例化一次");
            } catch (Exception e) {
                e.printStackTrace();
            }
        return songListManager;
    }
    private SongListManager(Context context,QMUIGroupListView mGroupListView){
        this.mGroupListView=mGroupListView;
        this.context=context;
    }
   public void initList(final LinkedList<MusicData> linkedList, boolean isplaying){
        mGroupListView.removeAllViews();
       QMUIGroupListView.Section section=QMUIGroupListView.newSection(context)
               .setTitle("音乐列表");
       int count=0;
       for (MusicData musicData : linkedList) {
           QMUICommonListItemView itemWithDetailBelow = mGroupListView.createItemView(musicData.name);
           itemWithDetailBelow.setOrientation(QMUICommonListItemView.VERTICAL);
           itemWithDetailBelow.setDetailText(musicData.singer);//默认文字在左边   描述文字在标题下边
           itemWithDetailBelow.setImageDrawable(context.getDrawable(isplaying?R.drawable.nowpaying_green:musicData.coverImage));
           isplaying=false;
           final int finalCount = count;
           View.OnClickListener onClickListener = new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (v instanceof QMUICommonListItemView) {
                       playNextMusic(finalCount,linkedList);
                   }
               }
           };//默认文字在左边   自定义加载框按钮
           section.addItemView(itemWithDetailBelow, onClickListener);
           count++;


           //占位
           QMUICommonListItemView itemWithDetailBelow2 = mGroupListView.createItemView("  ");
           itemWithDetailBelow2.setOrientation(QMUICommonListItemView.VERTICAL);
           itemWithDetailBelow2.setMaxHeight(40);
           itemWithDetailBelow2.setDetailText("   ");//默认文字在左边   描述文字在标题下边
           section.addItemView(itemWithDetailBelow2, null);
       }
       section.addTo(mGroupListView);


    }

   private void playNextMusic(int count,LinkedList<MusicData> linkedList){
        //播放第几个
       //插播模式--速度更快--主要是稳定.....
       MusicData musicData=linkedList.remove(count);
       linkedList.addFirst(musicData);
//       for (int i = 0; i < count; i++) {
//           linkedList.addLast(linkedList.pollFirst());
//       }
       change=true;
    }


}