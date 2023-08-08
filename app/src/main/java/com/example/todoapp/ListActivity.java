package com.example.todoapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.model.TodoItem;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private static final String PREF_NAME = "TodoListPref";
    //private static final String PREF_TODO_KEY_ITEMS = "todoItems";
    private TableLayout tableLayout;
    private EditText editText;
    private List<TodoItem> todoItems;
    private Map<String, List<TodoItem>> todoItemsMap;
    private String selectedListName;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linearlayout);

        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
        selectedListName = getIntent().getStringExtra("List Name");
        todoItemsMap = new HashMap<>();

        if (null != selectedListName) {
            final TextView textView = findViewById(R.id.textViewListName);

            textView.setText(selectedListName);
        }

        final Button addButton = findViewById(R.id.button);
        final ImageButton backToMenu = findViewById(R.id.backToMenu);

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTodoItem();
            }
        });

        loadTodoItems(selectedListName);
        refreshTableLayout();
    }

    /**
     * <p>
     * Refreshes the table layout for new list
     * </p>
     */
    private void refreshTableLayout() {
        tableLayout.removeAllViews();
        todoItems = todoItemsMap.get(selectedListName);

        if (null != todoItems) {

            for (final TodoItem todoItem : todoItems) {
                createTableRow(todoItem);
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
            todoItems = todoItemsMap.get(selectedListName);

            if (null == todoItems) {
                todoItems = new ArrayList<>();

                todoItemsMap.put(selectedListName, todoItems);
            }
            todoItems.add(todoItem);
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
            final Type type = new TypeToken<List<TodoItem>>() {}.getType();
            todoItems = new Gson().fromJson(todoItemsJson, type);

            todoItemsMap.put(selectedListName, todoItems);
        } else {
            todoItemsMap.put(selectedListName, new ArrayList<>());
        }
    }

    /**
     * <p>
     * Saves the data of the list name
     * </p>
     * @param selectedListName Represents the list name of the selected one by the user
     */
    private void saveTodoItems(final String selectedListName) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        todoItems = todoItemsMap.get(selectedListName);
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
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                final TextView textView = (TextView) tableRow.getChildAt(1);

                if (isChecked) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
            }
        });

        tableRow.addView(checkBox);

        final TextView todoView = new TextView(this);
        todoView.setText(todoItem.getText());
        tableRow.addView(todoView);

        final ImageView closeIcon = new ImageView(this);
        closeIcon.setImageResource(R.drawable.baseline_close_24);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                removeItem(tableRow);
            }
        });

        tableRow.addView(closeIcon);
        tableLayout.addView(tableRow);
        editText.getText().clear();
    }

    /**
     * <p>
     * Removes an item in the table layout
     * </p>
     *
     * @param tableRow Represents table row object
     */
    private void removeItem(final TableRow tableRow) {
        tableLayout.removeView(tableRow);
        saveTodoItems(selectedListName);
        todoItems = todoItemsMap.get(selectedListName);

        if (null != todoItems) {
            final int index = tableLayout.indexOfChild(tableRow) - 1;

            if (0 <= index && index < todoItems.size()) {
                todoItems.remove(index);
                saveTodoItems(selectedListName);
            }
        }
    }
}

