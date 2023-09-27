package com.example.todoapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.backendservice.AuthenticationService;
import com.example.todoapp.backendservice.TodoItemService;
import com.example.todoapp.model.Filter;
import com.example.todoapp.model.Query;
import com.example.todoapp.model.Sort;
import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.TodoList;
import com.example.todoapp.todoadapter.ItemTouchHelperCallBack;
import com.example.todoapp.todoadapter.OnItemClickListener;
import com.example.todoapp.todoadapter.TodoAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PaginationActivity extends AppCompatActivity {

    private TodoAdapter todoAdapter;
    private EditText editText;
    private TodoList todoList;
    private Query query;
    private List<TodoItem> todoItems;
    private String selectedProjectId;
    private String token;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filterlayout);

        editText = findViewById(R.id.todoEditText);
        selectedProjectId = getIntent().getStringExtra(getString(R.string.project_id));
        token = getIntent().getStringExtra(getString(R.string.token));
        final String selectedProjectName = getIntent().getStringExtra(getString(R.string.project_name));
        todoList = new TodoList();
        query = todoList.getQuery();
        searchView = findViewById(R.id.searchView);
        statusSpinner = findViewById(R.id.statusSpinner);
        pageFilter = findViewById(R.id.pageFilter);
        prevButton = findViewById(R.id.previous_button);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        nextButton = findViewById(R.id.next_button);
        pageNumber = findViewById(R.id.pageNumber);
        final ImageButton filterButton = findViewById(R.id.filterButton);
        final Button addButton = findViewById(R.id.button);
        final ImageButton backToMenu = findViewById(R.id.menuButton);
        final ImageButton addSymbol = findViewById(R.id.addButton);
        todoItems = todoList.getAllItems(selectedProjectId);
        pageSize = Integer.parseInt(pageFilter.getSelectedItem().toString());

        if (null != selectedProjectName) {
            final TextView textView = findViewById(R.id.textViewListName);

            textView.setText(selectedProjectName);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoItems);

        recyclerView.setAdapter(todoAdapter);
        final ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(todoAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
        loadTodoItemsFromDataBase();
        applyFontToAllLayouts();
        applyFontSize();
        applyColorToComponent();
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
        todoAdapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onCheckBoxClick(final TodoItem todoItem) {
                updateItemStatus(todoItem);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCloseIconClick(final TodoItem todoItem) {
                removeTodoItem(todoItem);
                todoList.remove(todoItem.getId());
                todoItems.remove(todoItem);
                todoAdapter.notifyDataSetChanged();

                if (currentPage > getTotalPages()) {
                    currentPage = getTotalPages();
                }
                refreshLayout();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onItemOrderUpdateListener(final TodoItem fromItem, final TodoItem toItem) {
                updateItemOrder(fromItem, toItem);
            }
        });

        pageFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());

                if (currentPage > getTotalPages()) {
                    currentPage = getTotalPages();
                }
                refreshLayout();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showCompleted = position == 1;
                showNotCompleted = position == 2;

                filterItemState();
                refreshLayout();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showCompleted = false;
                showNotCompleted = false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                filterTableLayout(query.toLowerCase());

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterTableLayout(newText.toLowerCase());

                return true;
            }
        });
        nextButton.setOnClickListener(view -> {
            if ((currentPage * pageSize) < todoItems.size()) {
                currentPage++;
                loadTodoItemsFromDataBase();
                updatePageNumber(pageNumber);
            }
        } );
        prevButton.setOnClickListener(view -> {
            if (currentPage > 1) {
                currentPage--;
                loadTodoItemsFromDataBase();
                updatePageNumber(pageNumber);
            }
        });
    }

    private void updateItemStatus(final TodoItem todoItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.updateStatus(todoItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void updateItemOrder(final TodoItem fromItem, final TodoItem toItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.updateOrder(fromItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
        itemService.updateOrder(toItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void removeTodoItem(final TodoItem todoItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.delete(todoItem.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                showSnackBar(getString(R.string.removed_project));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void loadTodoItemsFromDataBase() {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.getAll(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                todoItems = parseItemsFromJson(responseBody);

                if (! todoItems.isEmpty()) {
                    todoList.setAllItems(todoItems);
                    refreshLayout();
                    updatePageNumber(pageNumber);
                } else {
                    pageNumber.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                    prevButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private List<TodoItem> parseItemsFromJson(final String responseBody) {
        final List<TodoItem> todoItemList = new ArrayList<>();

        try {
            final JSONObject responseJson = new JSONObject(responseBody);
            final JSONArray data = responseJson.getJSONArray(getString(R.string.data));

            for (int i = 0; i < data.length(); i++) {
                final JSONObject projectJson = data.getJSONObject(i);

                if (null != selectedProjectId && selectedProjectId.equals(
                        projectJson.getString(getString(R.string.json_id)))) {
                    final TodoItem todoItem = new TodoItem(projectJson.getString(
                            getString(R.string.json_name)));

                    todoItem.setId(projectJson.getString(getString(R.string.id)));
                    todoItem.setParentId(selectedProjectId);
                    todoItem.setItemOrder((long) projectJson.getInt(getString(R.string.sort_order)));
                    todoItem.setStatus(projectJson.getBoolean(getString(R.string.is_completed)) ?
                            TodoItem.StatusType.COMPLETED : TodoItem.StatusType.NON_COMPLETED);
                    todoItemList.add(todoItem);
                }
            }
            Collections.sort(todoItemList, new Comparator<TodoItem>() {
                @Override
                public int compare(final TodoItem item1, final TodoItem item2) {
                    return Long.compare(item1.getItemOrder(), item2.getItemOrder());
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return todoItemList;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addNewTodoItem() {
        final String todoText = editText.getText().toString();

        if (!todoText.isEmpty()) {
            final TodoItem todoItem = new TodoItem(todoText);
            final long itemOrder = todoAdapter.getItemCount() + 1;

            todoItem.setParentId(selectedProjectId);
            todoItem.setStatus(TodoItem.StatusType.NON_COMPLETED);
            todoItem.setItemOrder(itemOrder);
            todoList.add(todoItem);
            createTodoItem(todoItem);
        }
        editText.getText().clear();
        refreshLayout();
        updatePageNumber(pageNumber);
    }

    private void createTodoItem(final TodoItem todoItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url),
                token);

        itemService.create(todoItem.getLabel(), selectedProjectId,
                new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(final String responseBody) {
                        showSnackBar(getString(R.string.success));
                        todoItems = todoList.getAllItems(selectedProjectId);

                        pageNumber.setVisibility(View.VISIBLE);
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        todoAdapter.addTodoItems(todoItems);
                        loadTodoItemsFromDataBase();
                    }

                    @Override
                    public void onError(final String errorMessage) {
                        showSnackBar(errorMessage);
                    }
        });
    }

    private void refreshLayout() {
        int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());

        if (0 > startIndex) {
            startIndex = 0;
        }
        final List<TodoItem> currentPageItems = todoItems.subList(startIndex, endIndex);

        todoAdapter.addTodoItems(currentPageItems);
        updateButtonVisibility();
    }

    private void filterItemState() {
        final Filter filterObj = query.getFilterObj();

        filterObj.setAttribute(getString(R.string.status));

        if (showCompleted) {
            filterObj.setValues(Collections.singletonList(getString(R.string.completed)));
        } else if (showNotCompleted) {
            filterObj.setValues(Collections.singletonList(getString(R.string.non_completed)));
        } else {
            filterObj.setValues(Collections.singletonList(getString(R.string.all)));
        }
        setSortingInfo();
        query.setFilterObj(filterObj);
        todoItems = todoList.filterAndSortItems();

        if (currentPage > getTotalPages()) {
            currentPage = getTotalPages();
        }
    }

    private void setSortingInfo() {
        final Sort sort = query.getSort();

        sort.setAttribute(getString(R.string.label));
        sort.setType(Sort.SortType.DESCENDING);
        query.setSort(sort);
    }

    private void filterTableLayout(final String searchItem) {
        query.setSearch(searchItem);
        setSortingInfo();
        todoItems = todoList.filterAndSortItems();

        refreshLayout();
        updatePageNumber(pageNumber);
    }

    private void updateButtonVisibility() {
        if (1 == currentPage) {
            prevButton.setColorFilter(Color.GRAY);
            prevButton.setEnabled(false);
        } else {
            prevButton.setColorFilter(Color.BLACK);
            prevButton.setEnabled(true);
        }

        if (currentPage == getTotalPages()) {
            nextButton.setColorFilter(Color.GRAY);
            nextButton.setEnabled(false);
        } else {
            nextButton.setColorFilter(Color.BLACK);
            nextButton.setEnabled(true);
        }
    }

    @SuppressLint("DefaultLocale")
    private void updatePageNumber(final TextView pageNumber) {
        pageNumber.setText(String.format(getString(R.string.string_format), currentPage, getTotalPages()));
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) todoItems.size() / pageSize);
    }

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final RelativeLayout relativeLayout = findViewById(R.id.projectTitle);

        if (defaultColor == R.color.green) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.default_color));
        }
    }

    private void applyFontToAllLayouts() {
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void applyFontSize() {
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }
}