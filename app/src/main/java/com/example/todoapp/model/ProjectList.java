package com.example.todoapp.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectList {

    private List<Project> projectList;

    public ProjectList() {
        this.projectList = new ArrayList<>();
    }

    public void add(final Project project) {
        projectList.add(project);
    }

    public void remove(final String label) {
        projectList.removeIf(project -> project.getLabel().equals(label));
    }

    public List<Project> getAllList() {
        return projectList;
    }
}
