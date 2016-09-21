package com.itheima.mobilesafe.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.objects.Contact;

import java.util.List;

/**
 * Created by Catherine on 2016/9/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.MyViewHolder> {

    private Context ctx;
    private List<Contact> mDatas;

    public BlacklistAdapter(Context ctx, List<Contact> mDatas, OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
        this.ctx = ctx;
        this.mDatas = mDatas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                ctx).inflate(R.layout.list_item_contact, parent,
                false));
        return holder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<mDatas.get(position).phone.size();i++ ){
            sb.append(mDatas.get(position).phone.get(i));
            if(i!=mDatas.get(position).phone.size()-1)
                sb.append("\n");
        }

        holder.tv_name.setText(mDatas.get(position).name);
        holder.tv_phone.setText(sb.toString());
        if (mDatas.get(position).photo != null) {
            holder.iv_photo.setImageBitmap(mDatas.get(position).photo);
        } else {
            holder.iv_photo.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.profile));
        }

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
        ImageView iv_photo;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
        }
    }

}