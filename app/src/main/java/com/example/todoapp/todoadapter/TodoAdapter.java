package com.example.todoapp.todoadapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.TypeFaceUtil;
import com.example.todoapp.dao.ItemDao;
import com.example.todoapp.model.TodoItem;
import com.example.todoapp.projectadapter.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<TodoItem> todoItems;
    private final ItemDao itemDao;
    private OnItemClickListener listener;

    public TodoAdapter(final List<TodoItem> todoItems, final ItemDao itemDao) {
        this.itemDao = itemDao;
        this.todoItems = todoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final TodoItem todoItem = todoItems.get(position);
        final Typeface typeface = TypeFaceUtil.getSelectedTypeFace();
        final float fontSize = TypeFaceUtil.getSelectedFontSize();

        if (null != typeface) {
            holder.todoTextView.setTypeface(typeface);
        } else if (0 != fontSize){
            holder.todoTextView.setTextSize(fontSize);
        }
        holder.bind(todoItem, listener);
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public void setOnClickListener(final OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(todoItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTodoItems(final List<TodoItem> todoListItems) {
        todoItems.clear();
        todoItems.addAll(todoListItems);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView todoTextView;
        private final CheckBox checkBox;
        private final ImageButton todoRemoveButton;

        public ViewHolder(final View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.todoTextView);
            checkBox = itemView.findViewById(R.id.todoCheckBox);
            todoRemoveButton = itemView.findViewById(R.id.todoRemoveButton);
        }

        public void bind(final TodoItem todoItem, final OnItemClickListener listener) {
            checkBox.setChecked(todoItem.getStatus() == TodoItem.StatusType.COMPLETED);
            todoTextView.setText(todoItem.getLabel());
            todoTextView.setTextColor(todoItem.getStatus() == TodoItem.StatusType.COMPLETED
                    ? Color.GRAY : Color.BLACK);
            checkBox.setOnClickListener(view -> listener.onCheckBoxClick(todoItem));
            todoRemoveButton.setOnClickListener(view -> listener.onCloseIconClick(todoItem));
        }
    }


}