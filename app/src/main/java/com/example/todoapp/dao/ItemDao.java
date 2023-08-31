package com.example.todoapp.dao;

import com.example.todoapp.model.TodoItem;

import java.util.List;

public interface ItemDao {

    long insert(final TodoItem todoItem);
    List<TodoItem> getAllTodoItems();
    List<TodoItem> getTodoItemsForProject(final Long projectId);
    long delete(final Long id);
    void update(final TodoItem todoItem);
    void open();
    void close();
}
