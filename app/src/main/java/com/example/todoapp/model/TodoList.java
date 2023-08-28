package com.example.todoapp.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TodoList {

    private final Map<String, List<TodoItem>> todoItemsMap;
    private final Query query;

    public TodoList() {
        this.todoItemsMap = new HashMap<>();
        this.query = new Query();
    }

    public void add(final String listName, TodoItem todoItem) {
        final List<TodoItem> todoItems = todoItemsMap.getOrDefault(listName, new ArrayList<>());
        assert todoItems != null;

        todoItems.add(todoItem);
        todoItemsMap.put(listName, todoItems);
    }

    public void remove(final String listName, TodoItem todoItem) {
        final List<TodoItem> todoItems = todoItemsMap.get(listName);

        if (null != todoItems) {
            todoItems.remove(todoItem);
        }
    }

    public List<TodoItem> getAllList(final String listName) {
        return todoItemsMap.getOrDefault(listName, new ArrayList<>());
    }

    public void setAllList(final String listName, List<TodoItem> todoItems) {
        todoItemsMap.put(listName, todoItems);
    }

    public long getCompletedCount(final String listName) {
        final List<TodoItem> items = todoItemsMap.get(listName);

        if (null != items) {
            return items.stream().filter(TodoItem::isChecked).count();
        }

        return 0;
    }

    public List<TodoItem> filterAndSortItems(final String listName) {
        final Sort sort = query.getSort();
        final Filter filterObj = query.getFilterObj();
        final List<TodoItem> todoItems = todoItemsMap.get(listName);
        List<TodoItem> filteredItems;
        assert todoItems != null;

        if (null != query.getSearch() && ! query.getSearch().isEmpty()) {
            filteredItems = filterSearchItems(todoItems, query.getSearch());
        } else {
            filteredItems = todoItems;
        }

        if (null != query.getSearch()) {
            if (sort.getType() == Sort.SortType.DESCENDING) {
                filteredItems.sort((item1, item2) -> item2.getLabel().compareTo(item1.getLabel()));
            } else if (sort.getType() == Sort.SortType.ASCENDING) {
                filteredItems.sort(Comparator.comparing(TodoItem::getLabel));
            }
        }

        if (null != filterObj && "status".equals(filterObj.getAttribute())) {

            if (filterObj.getValues().contains("Completed")) {
                filteredItems = filteredItems.stream().filter(TodoItem::isChecked)
                        .collect(Collectors.toList());
            } else if (filterObj.getValues().contains("Not Completed")) {
                filteredItems = filteredItems.stream().filter(todoItem -> !todoItem.isChecked())
                        .collect(Collectors.toList());
            }
        }

        return filteredItems.stream().skip(query.getSkip()).limit(query.getLimit())
                .collect(Collectors.toList());
    }

    private List<TodoItem> filterSearchItems(final List<TodoItem> todoItems,
                                             final String searchItem) {
        final List<TodoItem> filteredItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getLabel().toLowerCase().contains(searchItem)) {
                filteredItems.add(todoItem);
            }
        }

        return filteredItems;
    }

    public Query getQuery() {
        return query;
    }
}
