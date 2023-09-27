package com.example.todoapp.service;

import com.example.todoapp.model.Project;
import com.example.todoapp.model.ProjectList;

import java.util.List;

/**
 * <p>
 * Handles operation on projects within the application using {@link ProjectList} model
 * </p>
 *
 * @author Arun
 * @version 1.0
 */
public class ProjectService {

    private final ProjectList projectList;

    public ProjectService(final ProjectList projectList) {
        this.projectList = projectList;
    }

    /**
     * <p>
     * Adds a new project to the project list
     * </p>
     *
     * @param project Represents project object
     */
    public void addProject(final Project project) {
        projectList.add(project);
    }

    /**
     * <p>
     * Removes a project from the project list based on its name
     * </p>
     *
     * @param id Represents the id of the project
     */
    public void removeProject(final String id) {
        projectList.remove(id);
    }

    /**
     * <p>
     * Checks whether the project already exist in the project list
     * </p>
     *
     * @param projectName Represents name of the project
     * @return True if project is exists, false otherwise
     */
    public boolean projectExists(final String projectName) {
        return getAllProjects().stream().anyMatch(project -> project.getName()
                .equals(projectName));
    }

    /**
     * <p>
     * Retrieves a list of all project in the project list
     * </p>
     *
     * @return A list of projects
     */
    public List<Project> getAllProjects() {
        return projectList.getAllList();
    }
}
