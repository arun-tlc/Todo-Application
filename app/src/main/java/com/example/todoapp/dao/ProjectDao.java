package com.example.todoapp.dao;

import com.example.todoapp.model.Project;

public interface ProjectDao {

    long insert(final Project project);
    void open();
    void close();
}
