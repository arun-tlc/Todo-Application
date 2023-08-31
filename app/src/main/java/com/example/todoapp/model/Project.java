package com.example.todoapp.model;

import androidx.annotation.NonNull;

public class Project {

    private Long id;
    private String label;
    private Long userId;
    private Long projectOrder;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getProjectOrder() {
        return projectOrder;
    }

    public void setProjectOrder(final Long projectOrder) {
        this.projectOrder = projectOrder;
    }

    @NonNull
    public String toString() {
        return label;
    }
}
