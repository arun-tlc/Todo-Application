package com.example.todoapp.projectadapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class DragItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;

    public DragItemTouchHelper(final ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull final RecyclerView recyclerView,
                                @NonNull final RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull final RecyclerView recyclerView,
                          @NonNull final RecyclerView.ViewHolder source,
                          @NonNull final RecyclerView.ViewHolder target) {
        adapter.onItemMove(source.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());

        return true;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, final int direction) {}

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
