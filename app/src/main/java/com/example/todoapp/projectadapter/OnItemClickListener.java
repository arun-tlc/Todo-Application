package com.example.todoapp.projectadapter;

import com.example.todoapp.model.Project;

public interface OnItemClickListener {

    void onItemClick(final int position);

    void onRemoveButtonClick(final Project project);

    void onProjectOrderUpdateListener(final Project fromProject, final Project toProject);
}
