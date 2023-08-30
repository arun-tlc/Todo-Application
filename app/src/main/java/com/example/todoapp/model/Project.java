package com.example.todoapp.model;

import androidx.annotation.NonNull;

public class Project {

    private Long id;
    private String label;

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

    @NonNull
    public String toString() {
        return label;
    }
}
