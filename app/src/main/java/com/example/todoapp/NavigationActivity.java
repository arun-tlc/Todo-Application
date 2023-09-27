package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.backendservice.AuthenticationService;
import com.example.todoapp.backendservice.TodoProject;
import com.example.todoapp.controller.NavigationController;
import com.example.todoapp.model.Project;
import com.example.todoapp.model.ProjectList;
import com.example.todoapp.model.UserProfile;
import com.example.todoapp.projectadapter.DragItemTouchHelper;
import com.example.todoapp.projectadapter.OnItemClickListener;
import com.example.todoapp.projectadapter.ProjectAdapter;
import com.example.todoapp.service.ProjectService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private ProjectAdapter projectAdapter;
    private List<Project> projects;
    private UserProfile userProfile;
    private ProjectList projectList;
    private String token;
    private Button addProject;
    private NavigationController navigationController;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private LinearLayout addLayout;
    private EditText projectEditText;
    private Spinner fontFamily;
    private Spinner fontSizeSpinner;
    private Spinner defaultColorSpinner;
    private String selectedFontFamily;
    private String selectedColor;
    private String selectedFontSize;
    private boolean isFontFamilyItemSelected;
    private boolean isFontSizeItemSelected;
    private boolean isDefaultColorItemSelected;

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
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        final RecyclerView recyclerView = findViewById(R.id.nameListView);
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        token = getIntent().getStringExtra(getString(R.string.token));
        userTitle = findViewById(R.id.userTitle);
        addLayout = findViewById(R.id.addLayout);
        projectEditText = findViewById(R.id.projectEditText);
        addProject = findViewById(R.id.addProject);
        userProfile = new UserProfile();

        if (null == projectList) {
            projectList = new ProjectList();
        }
        projects = projectList.getAllList();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(projects);

        recyclerView.setAdapter(projectAdapter);
        final ItemTouchHelper.Callback callback = new DragItemTouchHelper(projectAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
        getUserDetail();
        loadProjectsFromDataBase();
        navigationController = new NavigationController(this,
                new ProjectService(projectList));
        final ImageButton menuButton = findViewById(R.id.menuButton);
        final ImageButton logout = findViewById(R.id.logout);

        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        final ImageButton editButton = findViewById(R.id.editIcon);

        editButton.setOnClickListener(view -> goToProfilePage());
        final TextView addListName = findViewById(R.id.addListName);

        addListName.setOnClickListener(view -> addLayout.setVisibility(addLayout
                .getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
        addProject.setOnClickListener(view -> {
            final String projectName = projectEditText.getText().toString();

            if (! projectName.isEmpty()) {
                addProjectToList(projectName);
                projectEditText.setText("");
            }
        });
        logout.setOnClickListener(view -> finish());
        projectAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                final Project selectedProject = projectList.getAllList().get(position);

                navigationController.onListItemClick(selectedProject);
            }

            @Override
            public void onRemoveButtonClick(final Project projectToRemove) {
                removeProject(projectToRemove);
            }

            @Override
            public void onProjectOrderUpdateListener(final Project fromProject,
                                                     final Project toProject) {
                updateProjectOrder(fromProject, toProject);
            }
        });
        getSystemSettings();
        final ImageButton settingButton = findViewById(R.id.settingButton);
        fontFamily = findViewById(R.id.fontFamily);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.font_family, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontFamily.setAdapter(adapter);

        settingButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.END));
        fontFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String fontName = parent.getItemAtPosition(position).toString();

                if (isFontFamilyItemSelected && ! fontName.equals(selectedFontFamily)) {
                    selectedFontFamily = fontName;
                    final int fontId = getFondId(fontName);
                    final Typeface typeface = ResourcesCompat.getFont(NavigationActivity.this,
                            fontId);

                    TypeFaceUtil.setSelectedTypeFace(typeface);
                    applyFontToAllLayouts();
                    updateFontFamily(fontName);
                } else {
                    isFontFamilyItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        fontSizeSpinner = findViewById(R.id.fontSize);
        final ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.font_size, android.R.layout.simple_spinner_item);

        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(fontSizeAdapter);
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String fontSize = parent.getItemAtPosition(position).toString();

                if (isFontSizeItemSelected && ! fontSize.equals(selectedFontSize)) {
                    selectedFontSize = fontSize;
                    final float textSize = getFontSize(fontSize);

                    TypeFaceUtil.setSelectedFontSize(textSize);
                    applyFontSize();
                    updateFontSize((int) getFontSize(parent.getSelectedItem().toString()));
                } else {
                    isFontSizeItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        defaultColorSpinner = findViewById(R.id.defaultColor);
        final ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.default_color, android.R.layout.simple_spinner_item);

        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultColorSpinner.setAdapter(colorAdapter);
        defaultColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                final String color = parent.getItemAtPosition(position).toString();

                if (isDefaultColorItemSelected || color.equals(selectedColor)) {
                    selectedColor = color;
                    final int selectedColor = getColorResourceId(color);

                    TypeFaceUtil.setSelectedDefaultColor(selectedColor);
                    applyColorToComponents(selectedColor);
                    updateColor(color);
                } else {
                    isDefaultColorItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {}
        });
    }

//    private void updateSystemSettings() {
//        final String selectedFontFamily = fontFamily.getSelectedItem().toString();
//        final String selectedColor = defaultColorSpinner.getSelectedItem().toString();
//        final int fontSize = (int) getFontSize(fontSizeSpinner.getSelectedItem().toString());
//
//        sendSettings(selectedFontFamily, selectedColor, fontSize);
//    }

    private void updateFontFamily(final String fontFamily) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateFontFamily(fontFamily,
                new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(final String responseBody) {
                        showSnackBar(getString(R.string.update_success));
                    }

                    @Override
                    public void onError(final String errorMessage) {
                        showSnackBar(errorMessage);
                    }
                });
    }

    private void updateFontSize(final int fontSize) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateFontSize(fontSize, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                showSnackBar(getString(R.string.update_success));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void updateColor(final String color) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateColor(color, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                showSnackBar(getString(R.string.update_success));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void getSystemSettings() {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.getSystemSetting(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                handleSystemSettings(responseBody);
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void handleSystemSettings(final String responseBody) {
        try {
            final JSONObject responseJson = new JSONObject(responseBody);

            if (! responseJson.isNull(getString(R.string.data))) {
                final JSONObject data = responseJson.getJSONObject(getString(R.string.data));
                final String fontName = data.getString(getString(R.string.font_name));
                final int fontSize = data.getInt(getString(R.string.size));
                final String color = data.getString(getString(R.string.font_color));
                selectedFontFamily = fontName;
                selectedFontSize = mapFontSize(fontSize);
                selectedColor = color;

                updateFontFamilySpinner(fontName);
                updateFontSizeSpinner(selectedFontSize);
                updateColorSpinner(color);
                applySystemSettings(fontName, fontSize, color);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateColorSpinner(final String color) {
        final int position = getColorPosition(color);

        if (0 <= position) {
            defaultColorSpinner.setSelection(position);
        }
    }

    private void updateFontSizeSpinner(final String fontSize) {
        final int position = getFontSizePosition(fontSize);

        if (0 <= position) {
            fontSizeSpinner.setSelection(position);
        }
    }

    private void updateFontFamilySpinner(final String fontName) {
        final int position = getFontFamilyPosition(fontName);

        if (0 <= position) {
            fontFamily.setSelection(position);
        }
    }

    private int getFontFamilyPosition(final String fontName) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) fontFamily.getAdapter();

        return adapter.getPosition(fontName);
    }

    private int getFontSizePosition(final String fontSize) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) fontSizeSpinner.getAdapter();

        return adapter.getPosition(fontSize);
    }

    private int getColorPosition(final String color) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) defaultColorSpinner.getAdapter();

        return adapter.getPosition(color);
    }

    private void applySystemSettings(final String fontName, final int fontSize, final String color) {
        final int fontId = getFondId(fontName);
        final Typeface typeface = ResourcesCompat.getFont(NavigationActivity.this,
                fontId);
        final String sizeName = mapFontSize(fontSize);

        TypeFaceUtil.setSelectedTypeFace(typeface);
        TypeFaceUtil.setSelectedFontSize(getFontSize(sizeName));
        TypeFaceUtil.setSelectedDefaultColor(getColorResourceId(color));
        applyFontToAllLayouts();
        applyFontSize();
        applyColorToComponents(TypeFaceUtil.getSelectedDefaultColor());
    }

    private String mapFontSize(final int fontSize) {
        if (fontSize == (int) getResources().getDimension(R.dimen.text_small)) {
            return getString(R.string.small);
        } else if (fontSize == (int) getResources().getDimension(R.dimen.text_medium)) {
            return getString(R.string.medium);
        } else if (fontSize == (int) getResources().getDimension(R.dimen.text_large)) {
            return getString(R.string.large);
        } else {
            return getString(R.string.Default);
        }
    }

    private void updateProjectOrder(final Project fromProject, final Project toProject) {
        final TodoProject projectService = new TodoProject(getString(R.string.base_url), token);

        projectService.updateOrder(fromProject, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
        projectService.updateOrder(toProject, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void getUserDetail() {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.getUserDetail(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                setUpUserDetails(responseBody);
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void setUpUserDetails(final String responseBody) {
        try {
            final JSONObject responseJson = new JSONObject(responseBody);
            final JSONObject data = responseJson.getJSONObject(getString(R.string.data));

            userProfile.setId(data.getString(getString(R.string.id)));
            userProfile.setName(data.getString(getString(R.string.json_name)));
            userProfile.setTitle(data.getString(getString(R.string.json_title)));
            userProfile.setEmail(data.getString(getString(R.string.json_email)));
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeProject(final Project project) {
        final TodoProject projectService = new TodoProject(getString(R.string.base_url), token);

        navigationController.removeProject(project);
        projectService.delete(project.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                showSnackBar(getString(R.string.removed_project));
            }

            @Override
            public void onError(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });

    }

    private void applyColorToComponents(final int colorId) {
        final int selectedColor = ContextCompat.getColor(this, colorId);
        final RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        final RelativeLayout userProfile = findViewById(R.id.profileHeader);

        relativeLayout.setBackgroundColor(selectedColor);
        addProject.setBackgroundColor(selectedColor);
        userProfile.setBackgroundColor(selectedColor);
    }

    private int getColorResourceId(final String color) {
        switch (color) {
            case "Green":
                return R.color.green;
            case "Blue":
                return R.color.blue;
            default:
                return R.color.default_color;
        }
    }

    private void applyFontSize() {
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private float getFontSize(final String sizeName) {
        switch (sizeName) {
            case "Small":
                return getResources().getDimension(R.dimen.text_small);
            case "Medium":
                return getResources().getDimension(R.dimen.text_medium);
            case "Large":
                return getResources().getDimension(R.dimen.text_large);
            default:
                return getResources().getDimension(R.dimen.text_default);
        }
    }

    private void applyFontToAllLayouts() {
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private int getFondId(final String fontName) {
        switch (fontName) {
            case "Arial Black":
                return R.font.arial_black;
            case "Cikal Bakal":
                return R.font.cikal_bakal;
            case "Times New Roman":
                return R.font.times_new;
            case "roboto":
                return R.font.roboto;
            case "Aclonica":
                return R.font.aclonica;
            case "Patrick":
                return R.font.patrick_hand_sc;
            default:
                return R.font.roboto;
        }
    }


    private void loadProjectsFromDataBase() {
        final TodoProject projectService = new TodoProject(getString(R.string.base_url), token);

        projectService.getAll(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                projects = parseProjectsFromJson(responseBody);

                projectAdapter.clearProjects();
                projectAdapter.addProjects(projects);
            }

            @Override
            public void onError(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private List<Project> parseProjectsFromJson(final String responseBody) {
        final List<Project> projectList = new ArrayList<>();

        try {
            final JSONObject responseJson = new JSONObject(responseBody);
            final JSONArray data = responseJson.getJSONArray(getString(R.string.data));

            for (int i = 0; i < data.length(); i++) {
                final JSONObject projectJson = data.getJSONObject(i);
                final JSONObject additionalAttributes = projectJson.getJSONObject(
                        getString(R.string.attributes));

                if (userProfile.getId().equals(additionalAttributes
                        .getString(getString(R.string.created_by)))) {
                    final Project project = new Project();

                    project.setId(projectJson.getString(getString(R.string.id)));
                    project.setName(projectJson.getString(getString(R.string.json_name)));
                    project.setProjectOrder((long) projectJson.getInt(getString(R.string.sort_order)));
                    projectList.add(project);
                }
            }
            Collections.sort(projectList, new Comparator<Project>() {
                @Override
                public int compare(final Project project1, final Project project2) {
                    return Long.compare(project1.getProjectOrder(), project2.getProjectOrder());
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return projectList;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addProjectToList(final String projectName) {
        final Project project = new Project();
        final TodoProject projectService = new TodoProject(
                getString(R.string.base_url), token);

        project.setName(projectName);
        navigationController.addNewProject(project);
        projectService.create(project, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                loadProjectsFromDataBase();
                showSnackBar(getString(R.string.success));
                projectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void goToProfilePage() {
        UserProfileSingleton.getInstance().setUserProfile(userProfile);
        final Intent intent = new Intent(NavigationActivity.this,
                ProfileActivity.class);

        intent.putExtra(getString(R.string.token), token);
        startActivityIfNeeded(intent, REQUEST_CODE);
    }

    /**
     * <p>
     * Goes to the list of item creation page
     * </p>
     *
     * @param project Represents the list name
     */
    public void goToItemListPage(final Project project) {
        final Intent intent = new Intent(NavigationActivity.this,
                PaginationActivity.class);

        intent.putExtra(getString(R.string.project_id), project.getId());
        intent.putExtra(getString(R.string.project_name), project.getName());
        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
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
        showSnackBar(getString(R.string.project_exist));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final UserProfile userProfile = new UserProfile();

            userProfile.setName(data.getStringExtra(getString(R.string.user_name)));
            userProfile.setTitle(data.getStringExtra(getString(R.string.user_title)));
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
        }
    }

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}