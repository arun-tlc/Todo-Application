package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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

import com.example.todoapp.model.Filter;
import com.example.todoapp.model.Query;
import com.example.todoapp.model.Sort;
import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.TodoList;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class PaginationActivity extends AppCompatActivity {

    private static final String PREF_NAME = "TodoListPref";
    private TableLayout tableLayout;
    private EditText editText;
    private TodoList todoList;
    private Query query;
    private List<TodoItem> todoItems;
    private Long selectedProjectId;
    private String selectedProjectName;
    private SearchView searchView;
    private Spinner statusSpinner;
    private Spinner pageFilter;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private boolean showCompleted;
    private boolean showNotCompleted;
    private TextView pageNumber;
    private int currentPage = 1;
    private int pageSize;
    private static long id = 0L;
    private int lastKnownPage;

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
        selectedProjectId = getIntent().getLongExtra("Project Id", 0L);
        selectedProjectName = getIntent().getStringExtra("Project Name");
        todoList = new TodoList();
        query = todoList.getQuery();
        searchView = findViewById(R.id.searchView);
        statusSpinner = findViewById(R.id.statusSpinner);
        pageFilter = findViewById(R.id.pageFilter);
        prevButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        pageNumber = findViewById(R.id.pageNumber);
        pageSize = Integer.parseInt(pageFilter.getSelectedItem().toString());
        final ImageButton filterButton = findViewById(R.id.filterButton);

        if (null != selectedProjectName) {
            final TextView textView = findViewById(R.id.textViewListName);

            textView.setText(selectedProjectName);
        }
        final Button addButton = findViewById(R.id.button);
        final ImageButton backToMenu = findViewById(R.id.menuButton);
        final ImageButton addSymbol = findViewById(R.id.addButton);

        addSymbol.setOnClickListener(view -> {
            if (editText.getVisibility() == View.GONE) {
                editText.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
            }
        });
        backToMenu.setOnClickListener(view -> onBackPressed());
        addButton.setOnClickListener(view -> addNewTodoItem());
        filterButton.setOnClickListener(view -> {
            lastKnownPage = currentPage;

            if (searchView.getVisibility() == View.GONE) {
                searchView.setVisibility(View.VISIBLE);
                statusSpinner.setVisibility(View.VISIBLE);
                pageFilter.setVisibility(View.VISIBLE);
            } else {
                searchView.setVisibility(View.GONE);
                statusSpinner.setVisibility(View.GONE);
                pageFilter.setVisibility(View.GONE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                lastKnownPage = currentPage;

                filterTableLayout(query.toLowerCase());

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                lastKnownPage = currentPage;

                filterTableLayout(newText.toLowerCase());

                return true;
            }
        });
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                showCompleted = position == 1;
                showNotCompleted = position == 2;

                filterItemState();
                refreshTableLayout();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                showCompleted = false;
                showNotCompleted = false;
            }
        });
        pageFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());

                refreshTableLayout();
                updatePageNumber(pageNumber);
                final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

                if (currentPage > totalPages) {
                    currentPage = totalPages;

                    refreshTableLayout();
                    updatePageNumber(pageNumber);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {}
        });
        nextButton.setOnClickListener(view -> {
            if ((currentPage * pageSize) < todoItems.size()) {
                currentPage++;
                refreshTableLayout();
                updatePageNumber(pageNumber);
            }
        } );
        prevButton.setOnClickListener(view -> {
            if (currentPage > 1) {
                currentPage--;
                refreshTableLayout();
                updatePageNumber(pageNumber);
            }
        });
        loadTodoItems(selectedProjectName);

        if (null != todoItems) {
            refreshTableLayout();
            updatePageNumber(pageNumber);
            updateButtonVisibility();
        } else {
            pageNumber.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    private void filterItemState() {
        final Filter filterObj = query.getFilterObj();

        filterObj.setAttribute("status");

        if (showCompleted) {
            filterObj.setValues(Collections.singletonList("Completed"));
        } else if (showNotCompleted) {
            filterObj.setValues(Collections.singletonList("Not Completed"));
        } else {
            filterObj.setValues(Collections.singletonList("All"));
        }
        setSortingInfo();
        query.setFilterObj(filterObj);
        todoItems = todoList.filterAndSortItems();

        moveToLastPage();
    }

    private void moveToLastPage() {
        final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);
        currentPage = todoItems.isEmpty() ? 1 : Math.min(lastKnownPage, totalPages);

        if (todoItems.isEmpty()) {
            pageNumber.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            Toast.makeText(this, R.string.item_check, Toast.LENGTH_SHORT).show();
        } else {
            pageNumber.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    private void setSortingInfo() {
        final Sort sort = query.getSort();

        sort.setAttribute("label");
        sort.setType(Sort.SortType.DESCENDING);
        query.setSort(sort);
    }

    private void updateButtonVisibility() {
        final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

        if (1 == currentPage) {
            prevButton.setColorFilter(Color.GRAY);
            prevButton.setEnabled(false);
        } else {
            prevButton.setColorFilter(Color.BLACK);
            prevButton.setEnabled(true);
        }

        if (currentPage == totalPages) {
            nextButton.setColorFilter(Color.GRAY);
            nextButton.setEnabled(false);
        } else {
            nextButton.setColorFilter(Color.BLACK);
            nextButton.setEnabled(true);
        }
    }

    /**
     * <p>
     *  Updates the page number in the layout
     * </p>
     *
     * @param pageNumber Represents the layout view of the page number
     */
    @SuppressLint("DefaultLocale")
    private void updatePageNumber(final TextView pageNumber) {
        final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

        pageNumber.setText(String.format("%d / %d", currentPage, totalPages));
    }

    /**
     * <p>
     * Filters the searched item from the list of item
     * </p>
     *
     * @param searchItem Represents the searched items
     */
    private void filterTableLayout(final String searchItem) {
        tableLayout.removeAllViews();
        query.setSearch(searchItem);
        setSortingInfo();
        todoItems = todoList.filterAndSortItems();

        moveToLastPage();
        refreshTableLayout();
        updateButtonVisibility();
        updatePageNumber(pageNumber);
    }

    /**
     * <p>
     * Refreshes the table layout for new list
     * </p>
     */
    private void refreshTableLayout() {
        tableLayout.removeAllViews();
        final int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final TodoItem todoItem = todoItems.get(i);

            createTableRow(todoItem);
        }
        updateButtonVisibility();
    }

    /**
     * <p>
     * Adds the items for the todo list
     * </p>
     */
    private void addNewTodoItem() {
        final String todoText = editText.getText().toString();

        if (! todoText.isEmpty()) {
            final TodoItem todoItem = new TodoItem(todoText);

            todoItem.setParentId(selectedProjectId);
            todoItem.setId(++id);
            todoList.add(todoItem);
            todoItems = todoList.getAllItems(selectedProjectId);
            final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);
            pageNumber.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);

            if (1 == todoItems.size() % pageSize && currentPage == totalPages - 1) {
                currentPage = totalPages;
            }
            createTableRow(todoItem);
            editText.getText().clear();
            saveTodoItems(selectedProjectName);

            refreshTableLayout();
            updatePageNumber(pageNumber);
        }

    }

    private void loadTodoItems(final String selectedProjectName) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final String todoItemsJson = sharedPreferences.getString(selectedProjectName, null);

        if (null != todoItemsJson) {
            final Type listType = new TypeToken<List<TodoItem>>() {}.getType();
            todoItems = new Gson().fromJson(todoItemsJson, listType);

            todoList.setAllItems(todoItems);
        }
    }

    private void saveTodoItems(final String selectedProjectName) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        todoItems = todoList.getAllItems(selectedProjectId);
        final String todoItemsJson = new Gson().toJson(todoItems);

        editor.putString(selectedProjectName, todoItemsJson);
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

        checkBox.setChecked(todoItem.isChecked());
        final TextView todoView = new TextView(this);

        todoView.setTextColor(todoItem.isChecked() ? Color.GRAY : Color.BLACK);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todoItem.setChecked();
            todoView.setTextColor(todoItem.isChecked() ? Color.GRAY : Color.BLACK);
            saveTodoItems(selectedProjectName);
        });
        tableRow.addView(checkBox);
        todoView.setText(todoItem.getLabel());
        tableRow.addView(todoView);
        final ImageView closeIcon = new ImageView(this);

        closeIcon.setImageResource(R.drawable.baseline_close_24);
        closeIcon.setOnClickListener(view -> removeItem(tableRow, todoItem));
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
    private void removeItem(final TableRow tableRow, final TodoItem todoItem) {
        final int previousTotalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

        tableLayout.removeView(tableRow);
        todoList.remove(todoItem.getId());
        saveTodoItems(selectedProjectName);

        if (todoItems.isEmpty()) {
            pageNumber.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        } else {
            refreshTableLayout();
            final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            if (previousTotalPages > totalPages) {
                refreshTableLayout();
            }
            updatePageNumber(pageNumber);
            updateButtonVisibility();
        }
    }
}