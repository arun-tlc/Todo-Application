package com.example.todoapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.todoapp.controller.NavigationController;
import com.example.todoapp.model.Project;
import com.example.todoapp.model.ProjectList;
import com.example.todoapp.model.UserProfile;
import com.example.todoapp.service.ProjectService;

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
public class NavigationActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<Project> projects;
    private ProjectList projectList;
    private NavigationController navigationController;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;

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
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        userTitle = findViewById(R.id.userTitle);

        if (null == projectList) {
            projectList = new ProjectList();
        }
        projects = projectList.getAllList();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                extractProjectLabels(projects));

        listView.setAdapter(arrayAdapter);
        navigationController = new NavigationController(this,
                new ProjectService(projectList));
        final ImageButton menuButton = findViewById(R.id.menuButton);

        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        final ImageButton editButton = findViewById(R.id.editIcon);

        editButton.setOnClickListener(view -> goToProfilePage());
        final TextView addListName = findViewById(R.id.addListName);

        addListName.setOnClickListener(view -> navigationController.onAddListNameClick());
        final ListView listView = findViewById(R.id.nameListView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            final Project selectedProject = projectList.getAllList().get(position);

            navigationController.onListItemClick(selectedProject);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            final Project selectedProject = projects.get(position);
            final String projectName = selectedProject.getLabel();

            navigationController.onListItemLongClick(selectedProject);

            Toast.makeText(NavigationActivity.this, String.join(" ",
                    projectName, "ListName Removed"), Toast.LENGTH_LONG).show();

            return true;
        });
    }

    private void goToProfilePage() {
        final Intent intent = new Intent(NavigationActivity.this,
                ProfileActivity.class);

        intent.putExtra("Exist Name", userName.getText().toString());
        intent.putExtra("Exist Title", userTitle.getText().toString());
        startActivityIfNeeded(intent, REQUEST_CODE);
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
    public void goToItemListPage(final String name) {
        final Intent intent = new Intent(NavigationActivity.this,
                QueryActivity.class);

        intent.putExtra("List Name", name);
        startActivity(intent);
    }

    /**
     * <p>
     * Shows an dialog box to create list of names for the add list name
     * </p>
     */
    public void showAddNameDialog() {
        final EditText editText = new EditText(this);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle("Add List Name")
                .setView(editText)
                .setPositiveButton(R.string.add_view, (dialog, which) -> {
                    final String name = editText.getText().toString().trim();

                    navigationController.addNewProject(name);
                    arrayAdapter.clear();
                    arrayAdapter.addAll(extractProjectLabels(projects));
                    arrayAdapter.notifyDataSetChanged();
                })
                .setNegativeButton(R.string.cancel_view, null)
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

    /**
     * <p>
     * Displays a toast message to the user
     * </p>
     */
    public void showProjectExistMessage() {
        Toast.makeText(NavigationActivity.this, "Project Name Already Exists",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * <p>
     * Adds a project to the array adapter and update the views
     * </p>
     *
     * @param name Represents the name of the project
     */
    public void addProjectToList(final String name) {
        arrayAdapter.add(name);
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * <p>
     * Removes the name of the project from the array adapter and update the views
     * </p>
     *
     * @param projectName Represents the name of the project
     */
    public void removeProjectFromList(final String projectName) {
        arrayAdapter.remove(projectName);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final UserProfile userProfile = new UserProfile();

            userProfile.setName(data.getStringExtra("User Name"));
            userProfile.setTitle(data.getStringExtra("User Title"));
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
        }
    }
}