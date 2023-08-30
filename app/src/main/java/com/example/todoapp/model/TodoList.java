package com.example.todoapp.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TodoList {

    private List<TodoItem> todoItems;
    private final Query query;

    public TodoList() {
        this.todoItems = new ArrayList<>();
        this.query = new Query();
    }

    public void add(final TodoItem todoItem) {
        todoItems.add(todoItem);
    }

    public void remove(final Long id) {
        todoItems = todoItems.stream().filter(todoItem -> ! Objects.equals(todoItem.getId(), id))
                .collect(Collectors.toList());
    }

    public List<TodoItem> getAllItems(final Long parentId) {
        if (null == parentId) {
            return todoItems;
        }

        return todoItems.stream().filter(todoItem -> todoItem.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }

    public void setAllItems(final List<TodoItem> todoItemList) {
        todoItems.clear();
        todoItems.addAll(todoItemList);
    }

    public Query getQuery() {
        return query;
    }

    public List<TodoItem> filterAndSortItems() {
        final Sort sort = query.getSort();
        final Filter filterObj = query.getFilterObj();
        List<TodoItem> filteredItems;

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
}
