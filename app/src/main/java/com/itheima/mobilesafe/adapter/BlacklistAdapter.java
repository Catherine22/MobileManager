package com.itheima.mobilesafe.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.recycler_view.OnItemTouch;
import com.itheima.mobilesafe.utils.objects.BlockedCaller;

import java.util.Collections;
import java.util.List;

/**
 * Created by Catherine on 2016/9/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.MyViewHolder> implements OnItemTouch {

    private Context ctx;
    private List<BlockedCaller> mDatas;

    public BlacklistAdapter(Context ctx, List<BlockedCaller> mDatas, @Nullable OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
        this.ctx = ctx;
        this.mDatas = mDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                ctx).inflate(R.layout.list_item_blacklist, parent,
                false));
        return holder;
    }

    /**
     * 取得交换过、移除过的正确的列表
     *
     * @return
     */
    public List<BlockedCaller> getList() {
        return mDatas;
    }

    public void addItem(BlockedCaller item) {
        mDatas.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 交换items(同时各自的position也会交换)
     *
     * @param fromPosition
     * @param toPosition
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDatas, fromPosition, toPosition);
        //非常重要，调用后Adapter才能知道发生了改变。
        notifyItemMoved(fromPosition, toPosition);
        mOnItemClickLitener.onItemSwap(fromPosition, toPosition);
    }

    /**
     * 滑动事件(移除该item)
     *
     * @param position
     */
    @Override
    public void onItemDismiss(int position) {
        mOnItemClickLitener.onItemDismiss(position, mDatas.get(position));
        mDatas.remove(position);
        //非常重要，调用后Adapter才能知道发生了改变。
        notifyItemRemoved(position);
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        void onItemSwap(int fromPosition, int toPosition);

        void onItemDismiss(int position, BlockedCaller item);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_name.setText(mDatas.get(position).getName());
        holder.tv_phone.setText(mDatas.get(position).getNumber());

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_phone;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        }
    }

}