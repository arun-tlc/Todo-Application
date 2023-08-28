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

    private ProjectList projectList;

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
     * @param projectName Represents the name of the project
     */
    public void removeProject(final String projectName) {
        projectList.remove(projectName);
    }

    /**
     * <p>
     * Checks whether the project already exist in the project list
     * </p>
     *
     * @param existingProject Represents project object
     * @return True if project is exists, false otherwise
     */
    public boolean projectExists(final Project existingProject) {
        return getAllProjects().stream().anyMatch(project -> project.getLabel()
                .equals(existingProject.getLabel()));
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
