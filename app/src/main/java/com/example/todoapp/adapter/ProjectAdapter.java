package com.example.todoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.model.Project;

import java.util.Collections;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private final List<Project> projects;
    private final LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(final int position);
        void onItemLongClick(final int position);
    }

    public ProjectAdapter(final Context context, final List<Project> projects) {
        this.inflater = LayoutInflater.from(context);
        this.projects = projects;
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
       final View view = inflater.inflate(R.layout.project_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Project project = projects.get(position);

        holder.projectNameTextView.setText(project.getLabel());
        holder.itemView.setOnClickListener(view -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemLongClick(position);
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void swapItems(final int fromPosition, final int toPosition) {
        Collections.swap(projects, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView projectNameTextView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.projectNameTextView);
        }
    }
}
