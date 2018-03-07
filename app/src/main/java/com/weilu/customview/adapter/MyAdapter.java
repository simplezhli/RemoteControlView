package com.weilu.customview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.weilu.customview.R;
import com.weilu.customview.bean.DraggableInfo;
import com.weilu.customview.widget.DraggableButton;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context mContext;
    private List<DraggableInfo> mData = new ArrayList<>();
    
    public MyAdapter(Context mContext, List<DraggableInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {

        private DraggableButton mButton;
        
        ViewHolder(View itemView) {
            super(itemView);
            mButton = itemView.findViewById(R.id.bt);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
        if (mData.get(position).getType() == 1){
            holder.mButton.setText(mData.get(position).getText());
        }else {
            holder.mButton.setImageResource(mData.get(position).getPic());
        }
        
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemLongClickListener.onItemLongClick(null, v, position, position);
               
                return false;
            }
        });
    }

    public DraggableInfo getItem(int position) {
        return mData.get(Math.max(0, position));
    }

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }
    
    @Override
    public int getItemCount() {
        return mData.size();
    }

}