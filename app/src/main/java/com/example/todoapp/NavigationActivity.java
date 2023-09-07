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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.projectadapter.DragItemTouchHelper;
import com.example.todoapp.projectadapter.ProjectAdapter;
import com.example.todoapp.controller.NavigationController;
import com.example.todoapp.dao.ProjectDao;
import com.example.todoapp.dao.UserDao;
import com.example.todoapp.dao.impl.ProjectDaoImpl;
import com.example.todoapp.dao.impl.UserDaoImpl;
import com.example.todoapp.model.Project;
import com.example.todoapp.model.ProjectList;
import com.example.todoapp.model.UserProfile;
import com.example.todoapp.service.ProjectService;

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
    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private List<Project> projects;
    private UserProfile userProfile;
    private UserDao userDao;
    private ProjectList projectList;
    private Button addProject;
    private NavigationController navigationController;
    private ProjectDao projectDao;
    private static Long id = 0L;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private Long userId;
    private LinearLayout addLayout;
    private EditText projectEditText;
    private boolean isFontFamilyItemSelected;
    private boolean isFontSizeItemSelected;

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
        recyclerView = findViewById(R.id.nameListView);
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        userTitle = findViewById(R.id.userTitle);
        addLayout = findViewById(R.id.addLayout);
        projectEditText = findViewById(R.id.projectEditText);
        addProject = findViewById(R.id.addProject);
        projectDao = new ProjectDaoImpl(this);
        userDao = new UserDaoImpl(this);
        userProfile = userDao.getUserProfile();

        if (null == projectList) {
            projectList = new ProjectList();
        }
        projects = projectList.getAllList();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(projects, projectDao);

        recyclerView.setAdapter(projectAdapter);
        final ItemTouchHelper.Callback callback = new DragItemTouchHelper(projectAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
        loadProjectsFromDataBase();
        navigationController = new NavigationController(this,
                new ProjectService(projectList));

        if (null != userProfile) {
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
            userId = userProfile.getId();
        }
        final ImageButton menuButton = findViewById(R.id.menuButton);

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
        projectAdapter.setOnItemClickListener(position -> {
            final Project selectedProject = projectList.getAllList().get(position);

            navigationController.onListItemClick(selectedProject);
        });
        final ImageButton settingButton = findViewById(R.id.settingButton);
        final Spinner fontFamily = findViewById(R.id.fontFamily);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.font_family, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontFamily.setAdapter(adapter);

        settingButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.END));
        fontFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFontFamilyItemSelected) {
                    final int fontId = getFondIdFromPosition(position);
                    final Typeface typeface = ResourcesCompat.getFont(NavigationActivity.this,
                            fontId);

                    TypeFaceUtil.setSelectedTypeFace(typeface);
                    applyFontToAllLayouts();
                } else {
                    isFontFamilyItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner fontSizeSpinner = findViewById(R.id.fontSize);
        final ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.font_size, android.R.layout.simple_spinner_item);

        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(fontSizeAdapter);
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFontSizeItemSelected) {
                    final float textSize = getFontSize(position);

                    TypeFaceUtil.setSelectedFontSize(textSize);
                    applyFontSize();
                } else {
                    isFontSizeItemSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        final Spinner defaultColorSpinner = findViewById(R.id.defaultColor);
        final ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.default_color, android.R.layout.simple_spinner_item);

        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultColorSpinner.setAdapter(colorAdapter);
        defaultColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int selectedColor = getColorResourceId(position);

                TypeFaceUtil.setSelectedDefaultColor(selectedColor);
                applyColorToComponents(selectedColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

    private int getColorResourceId(final int position) {
        switch (position) {
            case 0:
                return R.color.green;
            case 1:
                return R.color.blue;
            default:
                return R.color.default_color;
        }
    }

    private void applyFontSize() {
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private float getFontSize(final int position) {
        switch (position) {
            case 0:
                return getResources().getDimension(R.dimen.text_small);
            case 1:
                return getResources().getDimension(R.dimen.text_medium);
            case 2:
                return getResources().getDimension(R.dimen.text_large);
            default:
                return getResources().getDimension(R.dimen.text_default);
        }
    }

    private void applyFontToAllLayouts() {
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private int getFondIdFromPosition(final int position) {
        switch (position) {
            case 0:
                return R.font.arial_black;
//                setTheme(R.style.ArialBlack);
//                break;
            case 1:
                return R.font.cikal_bakal;
//                setTheme((R.style.CikalBakal));
//                break;
            case 2:
                return R.font.times_new;
//                setTheme(R.style.TimesNewRoman);
//                break;
            default:
                return R.font.font;
//                setTheme(R.style.Theme_TodoApp);
        }
    }


    private void loadProjectsFromDataBase() {
        projects = projectDao.getAllProjects();

        projectAdapter.clearProjects();
        projectAdapter.addProjects(projects);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addProjectToList(final String projectName) {
        final Project project = new Project();

        project.setId(++id);
        project.setLabel(projectName);
        project.setUserId(userId);
        final long projectOrder = projectAdapter.getItemCount() + 1;

        project.setProjectOrder(projectOrder);
        navigationController.addNewProject(project);
        final long projectId = projectDao.insert(project);

        if (-1 == projectId) {
            Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT)
                    .show();

            projectAdapter.notifyDataSetChanged();
        }
    }

    private void goToProfilePage() {
        final Intent intent = new Intent(NavigationActivity.this,
                ProfileActivity.class);

        intent.putExtra(String.valueOf(R.string.exist), userName.getText().toString());
        intent.putExtra("Exist Title", userTitle.getText().toString());
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
                DragAndDropActivity.class);

        intent.putExtra("Project Id", project.getId());
        intent.putExtra("Project Name", project.getLabel());
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
        Toast.makeText(NavigationActivity.this, "Project Name Already Exists",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final UserProfile userProfile = new UserProfile();

            userProfile.setName(data.getStringExtra("User Name"));
            userProfile.setTitle(data.getStringExtra("User Title"));
            userId = data.getLongExtra("User Id", 0L);
            userName.setText(userProfile.getName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIconText());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        projectDao.close();
    }
}