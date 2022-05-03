package com.sohn.data_maker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> implements GroupTouchHelperCallback.OnItemMoveListener{

    private ArrayList<String> itemList;
    private Context context;
    private View.OnClickListener onClickItem;
    private OnStartDragListener mStartDragListener;

    public interface OnStartDragListener{
        void onStartDrag(GroupListAdapter.ViewHolder holder);
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwiped(int Position);
    }

    public GroupListAdapter(Context context, ArrayList<String> itemList, View.OnClickListener onClickItem, OnStartDragListener startDragListener) {
        this.context = context;
        this.itemList = itemList;
        this.onClickItem = onClickItem;
        this.mStartDragListener = startDragListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // context 와 parent.getContext() 는 같다.
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_group, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemList.get(position);

        holder.textview.setText(item);
        holder.textview.setTag(item);
        holder.textview.setOnClickListener(onClickItem);
        holder.textview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mStartDragListener.onStartDrag(holder);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textview;

        public ViewHolder(View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.item_textview);
        }
    }
    @Override
    public void onItemMove(int fromPosition, int toPosition){
        mStartDragListener.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int Position){
        mStartDragListener.onItemSwiped(Position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<String> data){
        this.itemList = data;
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getItemList(){
        return itemList;
    }
}