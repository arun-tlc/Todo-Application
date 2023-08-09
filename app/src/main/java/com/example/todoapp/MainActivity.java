package com.example.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.todoapp.model.Project;
import com.example.todoapp.model.ProjectList;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Displays the activities of the todo application
 * </p>
 *
 * @author Arun
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<Project> projects;
    private ProjectList projectList;

    /**
     * <p>
     * Creates an interaction by listening the touch pad screen of android
     * </p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        listView = findViewById(R.id.nameListView);

        if (null == projectList) {
            projectList = new ProjectList();
        }

        projects = projectList.getAllList();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                extractProjectLabels(projects));

        listView.setAdapter(arrayAdapter);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        final ImageButton backMenuButton = findViewById(R.id.backMenuButton);
        backMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onBackPressed();
            }
        });

        final TextView addListName = findViewById(R.id.addListName);
        addListName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showAddNameDialog();
            }
        });

        final ListView listView = findViewById(R.id.nameListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final Project selectedProject = projectList.getAllList().get(position);

//                if (null != selectedProject) {
                    goToItemListPage(selectedProject.getLabel());
//                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                           final int position, final long id) {
                final Project selectedProject = projects.get(position);
                final String projectName = selectedProject.getLabel();

                projectList.remove(projectName);
                arrayAdapter.remove(projectName);
                arrayAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, String.join(" ",
                        projectName, "ListName Removed"), Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }

    /**
     * <p>
     * Extracts the labels of projects and returns a list of project labels
     * </p>
     *
     * @param projects Represents a list of project
     * @return The list of project labels
     */
    private List<String> extractProjectLabels(final List<Project> projects) {
        final List<String> projectLabels = new ArrayList<>();

        for (final Project project : projects) {
            projectLabels.add(project.getLabel());
        }

        return projectLabels;
    }

    /**
     * <p>
     * Goes to the list of item creation page
     * </p>
     *
     * @param name Represents the list name
     */
    private void goToItemListPage(final String name) {
        final Intent intent = new Intent(MainActivity.this, ListActivity.class);

        intent.putExtra("List Name", name);
        startActivity(intent);
    }

    /**
     * <p>
     * Shows an dialog box to create list of names for the add list name
     * </p>
     */
    private void showAddNameDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Add List Name")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final String name = editText.getText().toString().trim();

                        if (! name.isEmpty()) {
                            final Project project = new Project();

                            for (final Project existingProject : projects) {
                                if (existingProject.getLabel().equals(name)) {
                                    project.setChecked();
                                }
                            }

                            if (! project.isChecked()) {
                                project.setLabel(name);
                                projectList.add(project);

                                arrayAdapter.clear();
                                arrayAdapter.addAll(extractProjectLabels(projects));
//                            nameLists.add(name);
                                arrayAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Project Name Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * <p>
     * Goes to the previous layout page
     * </p>
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}