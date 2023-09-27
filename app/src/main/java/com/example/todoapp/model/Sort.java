package com.example.todoapp.model;

public class Sort {

    private String attribute;
    private SortType type;

    public enum SortType {
        ASCENDING,
        DESCENDING
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public SortType getType() {
        return type;
    }

    public void setType(final SortType type) {
        this.type = type;
    }
}

