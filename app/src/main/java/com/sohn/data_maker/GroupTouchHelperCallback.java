package com.sohn.data_maker;

import android.content.ClipData;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class GroupTouchHelperCallback extends ItemTouchHelper.Callback {

    private final OnItemMoveListener mItemMoveListener;
    public interface OnItemMoveListener{
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwiped(int Position);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, swipeFlags);
    }




    public GroupTouchHelperCallback(OnItemMoveListener listener){
        mItemMoveListener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
        mItemMoveListener.onItemMove(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
        System.out.println(viewHolder.toString());
        mItemMoveListener.onItemSwiped(viewHolder.getAbsoluteAdapterPosition());
    }

//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder holder, int a){
//        System.out.println("changed");
//    }


}
