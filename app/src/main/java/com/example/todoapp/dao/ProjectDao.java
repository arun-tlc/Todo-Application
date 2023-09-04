package com.example.todoapp.dao;

import com.example.todoapp.model.Project;

import java.util.List;

public interface ProjectDao {

    long insert(final Project project);
    List<Project> getAllProjects();
    void open();
    void close();
    void updateProjectsOrder(final Project project);
    void delete(Project projectToRemove);
}
