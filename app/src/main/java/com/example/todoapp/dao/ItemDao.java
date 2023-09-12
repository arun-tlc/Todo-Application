package com.example.todoapp.dao;

import com.example.todoapp.model.TodoItem;

import java.util.List;

public interface ItemDao {

    long insert(final TodoItem todoItem);
    List<TodoItem> getAllTodoItems();
    List<TodoItem> getTodoItemsForProject(final Long projectId);
    long delete(final Long id);
    void updateItemsStatus(final TodoItem todoItem);
    void open();
    void close();
    void updateItemsOrder(final TodoItem fromItem);
}
