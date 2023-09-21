package com.example.todoapp.todoadapter;

import com.example.todoapp.model.TodoItem;

public interface OnItemClickListener {

    void onCheckBoxClick(final TodoItem todoItem);

    void onCloseIconClick(final TodoItem todoItem);

    void onItemOrderUpdateListener(final TodoItem fromItem, final TodoItem toItem);
}
