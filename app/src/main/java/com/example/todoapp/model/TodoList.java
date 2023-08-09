package com.example.todoapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoList {

    private Map<String, List<TodoItem>> todoItemsMap;

    public TodoList() {
        todoItemsMap = new HashMap<>();
    }

    public void add(final String listName, TodoItem todoItem) {
        List<TodoItem> todoItems = todoItemsMap.getOrDefault(listName, new ArrayList<>());
        todoItems.add(todoItem);
        todoItemsMap.put(listName, todoItems);
    }

    public void remove(final String listName, TodoItem todoItem) {
        List<TodoItem> todoItems = todoItemsMap.get(listName);
        if (todoItems != null) {
            todoItems.remove(todoItem);
        }
    }

    public List<TodoItem> getAllList(final String listName) {
        return todoItemsMap.getOrDefault(listName, new ArrayList<>());
    }

    public void setAllList(final String listName, List<TodoItem> todoItems) {
        todoItemsMap.put(listName, todoItems);
    }
}
