package com.example.todoapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectList {

    private List<Project> projectList;

    public ProjectList() {
        this.projectList = new ArrayList<>();
    }

    public void add(final Project project) {
        projectList.add(project);
    }

    public void remove(final String id) {
        projectList = projectList.stream().filter(project -> ! id.equals(project.getId()))
                .collect(Collectors.toList());
        projectList.size();
    }

    public List<Project> getAllList() {
        return projectList;
    }
}