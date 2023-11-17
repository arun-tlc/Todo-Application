package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.todoapp.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private EditText editText;
    private List<TodoItem> todoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linearlayout);

        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
        todoItems = new ArrayList<>();

        final Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTodoItem();
            }
        });
    }

    private void addNewTodoItem() {
        final String todoText = editText.getText().toString();

        if (!todoText.isEmpty()) {
            final TodoItem todoItem = new TodoItem(todoText);
            todoItems.add(todoItem);
            createTableRow(todoItem);
            editText.getText().clear();
        }
    }


    private void createTableRow(final TodoItem todoItem) {
        final TableRow tableRow = new TableRow(this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        final CheckBox checkBox = new CheckBox(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
            public void onClick(View v) {
                removeItem(tableRow);
            }
        });

        tableRow.addView(closeIcon);

        tableLayout.addView(tableRow);

        editText.getText().clear();
    }

    private void removeItem(final TableRow tableRow) {
        tableLayout.removeView(tableRow);
    }
}