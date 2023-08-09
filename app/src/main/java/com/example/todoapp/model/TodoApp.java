package com.example.todoapp.model;

public class TodoApp {

    private final String title = "Todo List";
    private Project selectedProject;
    private ProjectList projectList;
    private TodoList todoList;

    public String getTitle() {
        return title;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(final Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    public ProjectList getProjectList() {
        return projectList;
    }

    public void setProjectList(final ProjectList projectList) {
        this.projectList = projectList;
    }

    public TodoList getTodoList() {
        return todoList;
    }

    public void setTodoList(final TodoList todoList) {
        this.todoList = todoList;
    }
}
