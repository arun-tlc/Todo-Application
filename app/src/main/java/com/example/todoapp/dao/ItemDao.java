package com.example.todoapp.dao;

import com.example.todoapp.model.TodoItem;

public interface ItemDao {

    long insert(final TodoItem todoItem);
    void open();
    void close();
}
