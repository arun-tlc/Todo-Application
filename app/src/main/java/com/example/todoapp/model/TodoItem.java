package com.example.todoapp.model;

import java.util.UUID;

public class TodoItem {

    private String id;
    private String label;
    private boolean isChecked;
    private int textColor;

    public TodoItem(final String label) {
        this.label = label;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(final int textColor) {
        this.textColor = textColor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(final boolean checked) {
        this.isChecked = checked;
    }

    public String toString() {
        return label;
    }
}

