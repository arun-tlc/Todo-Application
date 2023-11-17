package com.example.todoapp.model;

public class TodoItem {

    private String text;
    private boolean isChecked;

    public TodoItem(final String text) {
        this.text = text;
        this.isChecked = false;
    }

    public String getText() {
        return text;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(final boolean checked) {
        this.isChecked = checked;
    }
}

