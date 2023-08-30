package com.example.todoapp.controller;

import com.example.todoapp.NavigationActivity;
import com.example.todoapp.model.Project;
import com.example.todoapp.service.ProjectService;

/**
 * <p>
 * Facilitates communication between the view and the service
 * </p>
 *
 * @author Arun
 * @version 1.0
 */
public class NavigationController {

    private NavigationActivity activity;
    private ProjectService projectService;

    public NavigationController(final NavigationActivity activity,
                                final ProjectService projectService) {
        this.activity = activity;
        this.projectService = projectService;
    }

    /**
     * <p>
     * Handles the action when back menu button is click in the navigation activity
     * </p>
     */
    public void onBackMenuButtonClick() {
        activity.onBackPressed();
    }

    /**
     * <p>
     * Handles the action when add listName button is click in the navigation activity
     * </p>
     */
    public void onAddListNameClick() {
        activity.showAddNameDialog();
    }

    /**
     * <p>
     * Handles the action when list item is click in the navigation activity
     * </p>
     *
     * @param project Represents the selected project
     */
    public void onListItemClick(final Project project) {
        activity.goToItemListPage(project);
    }

    /**
     * <p>
     * Handles the action when list item is long click in the navigation activity
     * </p>
     *
     * @param project Represents the selected project
     */
    public void onListItemLongClick(final Project project) {
        projectService.removeProject(project.getId());
        activity.removeProjectFromList(project);
    }

    /**
     * <p>
     * Adds a new project to the list if project is not already exist in the list
     * </p>
     *
     * @param projectName Represents the name of the project
     */
    public void addNewProject(final Project project) {
        if (! projectService.projectExists(project.getLabel())) {
            projectService.addProject(project);
            activity.addProjectToList(project);
        } else {
            activity.showProjectExistMessage();
        }
    }
}