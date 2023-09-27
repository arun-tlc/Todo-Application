package com.example.todoapp.model;

public class TodoItem {

    private String id;
    private String label;
    private String parentId;
    private StatusType status;
    private Long itemOrder;
    private boolean isChecked;

    public enum StatusType {
        COMPLETED,
        NON_COMPLETED
    }

    public TodoItem(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked = ! this.isChecked;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(final StatusType status) {
        this.status = status;
    }

    public Long getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(final Long itemOrder) {
        this.itemOrder = itemOrder;
    }
}
