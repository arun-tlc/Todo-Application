package com.example.todoapp;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.dao.ItemDao;
import com.example.todoapp.dao.impl.ItemDaoImpl;
import com.example.todoapp.model.Filter;
import com.example.todoapp.model.Query;
import com.example.todoapp.model.Sort;
import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.TodoList;
import com.example.todoapp.todoadapter.ItemTouchHelperCallBack;
import com.example.todoapp.todoadapter.OnItemClickListener;
import com.example.todoapp.todoadapter.TodoAdapter;

import java.util.Collections;
import java.util.List;

public class DragAndDropActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private EditText editText;
    private TodoList todoList;
    private Query query;
    private List<TodoItem> todoItems;
    private ItemDao itemDao;
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
    private static Long id = 0L;
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

//        tableLayout = findViewById(R.id.tableLayout);
        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.todoEditText);
        selectedProjectId = getIntent().getLongExtra("Project Id", 0L);
        selectedProjectName = getIntent().getStringExtra("Project Name");
        todoList = new TodoList();
        query = todoList.getQuery();
        itemDao = new ItemDaoImpl(this);
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
        todoItems = todoList.getAllItems(null);
        final Button addButton = findViewById(R.id.button);
        final ImageButton backToMenu = findViewById(R.id.menuButton);
        final ImageButton addSymbol = findViewById(R.id.addButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoItems, itemDao);

        recyclerView.setAdapter(todoAdapter);
        final ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(todoAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                showCompleted = position == 1;
                showNotCompleted = position == 2;

                filterItemState();
                todoAdapter.notifyDataSetChanged();
//                refreshTableLayout();
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

//                refreshTableLayout();
                updatePageNumber(pageNumber);
                final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

                if (currentPage > totalPages) {
                    currentPage = totalPages;

//                    refreshTableLayout();
                    updatePageNumber(pageNumber);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {}
        });
        nextButton.setOnClickListener(view -> {
            if ((currentPage * pageSize) < todoItems.size()) {
                currentPage++;
//                refreshTableLayout();
                updatePageNumber(pageNumber);
            }
        } );
        prevButton.setOnClickListener(view -> {
            if (currentPage > 1) {
                currentPage--;
//                refreshTableLayout();
                updatePageNumber(pageNumber);
            }
        });
        todoAdapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onCheckBoxClick(TodoItem todoItem) {
                final int position = todoItems.indexOf(todoItem);

                if (-1 != position) {
                    final TodoItem updatedItem = todoItems.get(position);

                    updatedItem.setStatus(updatedItem.getStatus() == TodoItem.StatusType.COMPLETED
                            ? TodoItem.StatusType.NON_COMPLETED
                            : TodoItem.StatusType.COMPLETED);
                }
                itemDao.update(todoItem);
                todoAdapter.notifyItemChanged(position);
            }

            @Override
            public void onCloseIconClick(final TodoItem todoItem) {
                final int position = todoItems.indexOf(todoItem);

                if (-1 != position) {
                    final TodoItem removedItem = todoItems.remove(position);

                    todoList.remove(todoItem.getId());
                    itemDao.delete(removedItem.getId());
                    todoAdapter.notifyItemRemoved(position);
                    updatePageNumber(pageNumber);
                    updateButtonVisibility();
                }
            }
        });
        loadTodoItemsFromDatabase(selectedProjectId);

        if (! todoItems.isEmpty()) {
//            refreshTableLayout();
            updatePageNumber(pageNumber);
            updateButtonVisibility();
        } else {
            pageNumber.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    private void loadTodoItemsFromDatabase(final Long selectedProjectId) {
        todoItems = itemDao.getTodoItemsForProject(selectedProjectId);

        todoAdapter.addTodoItems(todoItems);

        if (null != todoItems) {
//            todoList.setAllItems(todoItems);


            updateButtonVisibility();
//            refreshTableLayout();
            updatePageNumber(pageNumber);
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
//        tableLayout.removeAllViews();
        query.setSearch(searchItem);
        setSortingInfo();
        todoItems = todoList.filterAndSortItems();

        moveToLastPage();
        todoAdapter.notifyDataSetChanged();
//        refreshTableLayout();
        updateButtonVisibility();
        updatePageNumber(pageNumber);
    }

    /**
     * <p>
     * Refreshes the table layout for new list
     * </p>
     */
    @SuppressLint("NotifyDataSetChanged")
//    private void refreshTableLayout() {
////        tableLayout.removeAllViews();
//        final int startIndex = (currentPage - 1) * pageSize;
//        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());
//        final List<TodoItem> visibleItems = todoItems.subList(startIndex, endIndex);
////
////        for (int i = startIndex; i < endIndex; i++) {
////            final TodoItem todoItem = todoItems.get(i);
////
//////            createTableRow(todoItem);
////        }
//        todoAdapter.addTodoItems(visibleItems);
//        todoAdapter.notifyDataSetChanged();
//        updateButtonVisibility();
//    }

    /**
     * <p>
     * Adds the items for the todo list
     * </p>
     */
    private void addNewTodoItem() {
        final String todoText = editText.getText().toString();

        if (! todoText.isEmpty()) {
            final TodoItem todoItem = new TodoItem(todoText);
            final long itemOrder = todoAdapter.getItemCount() + 1;

            todoItem.setParentId(selectedProjectId);
            todoItem.setId(++id);
            todoItem.setStatus(TodoItem.StatusType.NON_COMPLETED);
            todoItem.setItemOrder(itemOrder);
            todoList.add(todoItem);
            final long itemId = itemDao.insert(todoItem);
            todoItems = todoList.getAllItems(selectedProjectId);
            final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);
            pageNumber.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);

            if (-1 == itemId) {
                Toast.makeText(this,R.string.fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                todoAdapter.notifyDataSetChanged();
            }

            if (1 == todoItems.size() % pageSize && currentPage == totalPages - 1) {
                currentPage = totalPages;
            }
            editText.getText().clear();

//            refreshTableLayout();
            updateButtonVisibility();
            updatePageNumber(pageNumber);
        }

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

        todoList.remove(todoItem.getId());

        if (0 < itemDao.delete(todoItem.getId())) {
            Toast.makeText(this, R.string.delete, Toast.LENGTH_SHORT).show();
            todoItems = todoList.getAllItems(null);

//            refreshTableLayout();
            updatePageNumber(pageNumber);
            updateButtonVisibility();
        } else {
            Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
        }

        if (todoItems.isEmpty()) {
            pageNumber.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        } else {
//            refreshTableLayout();
            final int totalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

//            if (previousTotalPages > totalPages) {
//                refreshTableLayout();
//            }
            updatePageNumber(pageNumber);
            updateButtonVisibility();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != todoItems) {
            for (final TodoItem todoItem : todoItems) {
                itemDao.update(todoItem);
            }
        }
        itemDao.close();
    }
}