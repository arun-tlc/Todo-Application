package com.example.todoapp.model;

public class TodoItem {

    private String id;
    private String parentId;
    private final String label;
    private boolean isChecked;

    public TodoItem(final String label) {
        this.label = label;
    }

    public String getText() {
        return label;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked = ! this.isChecked;
    }
}

