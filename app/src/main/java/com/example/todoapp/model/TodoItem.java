package com.example.todoapp.model;

public class TodoItem {

    private Long id;
    private String label;
    private Long parentId;
    private boolean isChecked;

    public TodoItem(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked = ! this.isChecked;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }
}
