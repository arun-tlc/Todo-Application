package com.example.todoapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.TodoList;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private static final String PREF_NAME = "TodoListPref";
    private TableLayout tableLayout;
    private EditText editText;
    private TodoList todoList;
    private List<TodoItem> todoItems;
    private String selectedListName;
    private SearchView searchView;
    private Spinner statusSpinner;
    private ImageButton filterButton;
    private boolean showCompleted;
    private boolean showNotCompleted;

    /**
     * <p>
     * Creates an interaction by listening the touch pad screen of android
     * </p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filterlayout);

        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
        selectedListName = getIntent().getStringExtra("List Name");
        todoList = new TodoList();
        filterButton = findViewById(R.id.filterButton);
        searchView = findViewById(R.id.searchView);
        statusSpinner = findViewById(R.id.statusSpinner);

        if (null != selectedListName) {
            final TextView textView = findViewById(R.id.textViewListName);

            textView.setText(selectedListName);
        }

        final Button addButton = findViewById(R.id.button);
        final ImageButton backToMenu = findViewById(R.id.menuButton);

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onBackPressed();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addNewTodoItem();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getVisibility() == View.GONE) {
                    searchView.setVisibility(View.VISIBLE);
                    statusSpinner.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.GONE);
                    statusSpinner.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTableLayout(newText.toLowerCase());

                return true;
            }
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showCompleted = position == 1;
                showNotCompleted = position == 2;
                refreshTableLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showCompleted = false;
                showNotCompleted = false;
            }
        });

        loadTodoItems(selectedListName);
        refreshTableLayout();
    }

    /**
     * <p>
     * Filters the searched item from the list of item
     * </p>
     *
     * @param query Represents the searched items
     */
    private void filterTableLayout(final String query) {
        tableLayout.removeAllViews();

        for (final TodoItem todoItem : todoList.getAllList(selectedListName)) {

            if (todoItem.getLabel().toLowerCase().contains(query)) {
                createTableRow(todoItem);
            } else {
                Toast.makeText(this, "Items Not Found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * <p>
     * Refreshes the table layout for new list
     * </p>
     */
    private void refreshTableLayout() {
        tableLayout.removeAllViews();
        todoItems = todoList.getAllList(selectedListName);

        if (null != todoItems) {

            for (final TodoItem todoItem : todoItems) {
                if ((showCompleted && todoItem.isChecked()) ||
                        (showNotCompleted && ! todoItem.isChecked()) || (! showCompleted && ! showNotCompleted)) {

                    createTableRow(todoItem);
                }
            }
        }
    }

    /**
     * <p>
     * Adds the items for the todo list
     * </p>
     */
    private void addNewTodoItem() {
        final String todoText = editText.getText().toString();

        if (!todoText.isEmpty()) {
            final TodoItem todoItem = new TodoItem(todoText);

            todoList.add(selectedListName, todoItem);
            todoItems = todoList.getAllList(selectedListName);

            createTableRow(todoItem);
            editText.getText().clear();

            saveTodoItems(selectedListName);
        }
    }

    /**
     * <p>
     * Loads the data to shared preferences for data persistent
     * </p>
     *
     * @param selectedListName Represents the list name of the selected one by the user
     */
    private void loadTodoItems(final String selectedListName) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final String todoItemsJson = sharedPreferences.getString(selectedListName, null);

        if (null != todoItemsJson) {
            final Type type = new TypeToken<List<TodoItem>>() {
            }.getType();
            todoItems = new Gson().fromJson(todoItemsJson, type);

            if (null != todoItems) {

                for (final TodoItem todoItem : todoItems) {
                    int textColor = sharedPreferences.getInt(todoItem.getLabel() + "_textColor",
                            Color.GRAY);

                    todoItem.setTextColor(textColor);
                }
            }

            todoList.setAllList(selectedListName, todoItems);
        }
    }

    /**
     * <p>
     * Saves the data of the list name
     * </p>
     *
     * @param selectedListName Represents the list name of the selected one by the user
     */
    private void saveTodoItems(final String selectedListName) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        todoItems = todoList.getAllList(selectedListName);
        final String todoItemsJson = new Gson().toJson(todoItems);

        editor.putString(selectedListName, todoItemsJson);
        editor.apply();
    }

    /**
     * <p>
     * Creates an row for the table layout
     * </p>
     *
     * @param todoItem Represents {@link TodoItem} object
     */
    private void createTableRow(final TodoItem todoItem) {
        final TableRow tableRow = new TableRow(this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        final CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(getCheckBoxState(todoItem.getLabel()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                final TextView textView = (TextView) tableRow.getChildAt(1);

                todoItem.setChecked(isChecked);

                int textColor = todoItem.isChecked() ? Color.GRAY : Color.BLACK;

                textView.setTextColor(textColor);
                todoItem.setTextColor(textColor);

                saveCheckBoxState(todoItem.getLabel(), isChecked);
                saveTextColorState(todoItem.getLabel(), textColor);
            }
        });

        tableRow.addView(checkBox);

        final TextView todoView = new TextView(this);
        todoView.setText(todoItem.getLabel());
        tableRow.addView(todoView);

        final ImageView closeIcon = new ImageView(this);
        closeIcon.setImageResource(R.drawable.baseline_close_24);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                removeItem(tableRow, todoItem);
            }
        });

        tableRow.addView(closeIcon);
        tableLayout.addView(tableRow);
        editText.getText().clear();
    }

    /**
     * <p>
     * Saves the text color state of a project item
     * </p>
     *
     * @param label     Represents the label of the project
     * @param textColor The color value of the text
     */
    private void saveTextColorState(final String label, final int textColor) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(label + "_textColor", textColor);
        editor.apply();
    }

    /**
     * <p>
     * Retrieves the check box state of the project item
     * </p>
     *
     * @param label Represents the label of the project
     * @return {@code true} if the check box is checked, {@code false} otherwise
     */
    private boolean getCheckBoxState(final String label) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        return sharedPreferences.getBoolean(label + "_checkBox", false);
    }

    /**
     * <p>
     * Saves the check box state of a project item
     * </p>
     *
     * @param label     Represents the label of the project
     * @param isChecked Whether the checkbox is checked or not
     */
    private void saveCheckBoxState(final String label, final boolean isChecked) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(label + "_checkBox", isChecked);
        editor.apply();
    }

    /**
     * <p>
     * Removes an item in the table layout
     * </p>
     *
     * @param tableRow Represents table row object
     */
    private void removeItem(final TableRow tableRow, final TodoItem todoItem) {
        tableLayout.removeView(tableRow);
        todoList.remove(selectedListName, todoItem);
        saveTodoItems(selectedListName);
        refreshTableLayout();
    }
}

