package com.yuyang.messi.recycler;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import org.jetbrains.annotations.NotNull;

public class DragItemTouchCallBack extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter touchHelperAdapter;

    public DragItemTouchCallBack(ItemTouchHelperAdapter touchHelperAdapter) {
        this.touchHelperAdapter = touchHelperAdapter;
    }

    @Override
    public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.START | ItemTouchHelper.END;
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
    }

    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        touchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
    }

}
