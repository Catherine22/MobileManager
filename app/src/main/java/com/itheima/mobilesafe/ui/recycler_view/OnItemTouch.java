package com.itheima.mobilesafe.ui.recycler_view;

/**
 * Created by Yi-Jing on 2016/9/16.
 */

/**
 * 用于通知底层数据的更新
 */
public interface OnItemTouch {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}