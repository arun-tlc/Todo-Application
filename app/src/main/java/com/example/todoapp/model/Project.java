package com.example.todoapp.model;

import androidx.annotation.NonNull;

public class Project {

    private String id;
    private String name;
    private Long userId;
    private Long projectOrder;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
        return name;
    }
}
