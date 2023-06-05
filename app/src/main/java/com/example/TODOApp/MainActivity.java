package com.example.TODOApp;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> todoList;
    private ArrayList<String> filteredTodoList;
    private ArrayAdapter<String> todoAdapter;
    private EditText newItemEditText;
    private EditText searchEditText;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private ListView todoListView;
    private SharedPreferences sharedPreferences;

    private static final String TODO_LIST_KEY = "todo_list";

    private boolean isEditing = false;
    private int editItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoList = new ArrayList<>();
        filteredTodoList = new ArrayList<>();
        todoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredTodoList);

        newItemEditText = findViewById(R.id.newItemEditText);
        searchEditText = findViewById(R.id.searchEditText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        todoListView = findViewById(R.id.todoListView);
        todoListView.setAdapter(todoAdapter);

        sharedPreferences = getSharedPreferences("TodoApp", MODE_PRIVATE);

        addButton.setOnClickListener(v -> {
            if (isEditing) { // Updating mode
                String editedItem = newItemEditText.getText().toString();
                if (!editedItem.isEmpty()) {
                    todoList.set(editItemPosition, editedItem);
                    filterItems(searchEditText.getText().toString());
                    todoAdapter.notifyDataSetChanged();
                    newItemEditText.setText("");
                    isEditing = false;
                    editItemPosition = -1;
                    updateButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                    addButton.setVisibility(View.VISIBLE);
                    saveItems();
                    Toast.makeText(MainActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter an item", Toast.LENGTH_SHORT).show();
                }
            } else { // Adding mode
                String newItem = newItemEditText.getText().toString();
                if (!newItem.isEmpty()) {
                    todoList.add(newItem);
                    filterItems(searchEditText.getText().toString());
                    todoAdapter.notifyDataSetChanged();
                    newItemEditText.setText("");
                    saveItems();
                    Toast.makeText(MainActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                } else { // Empty item
                    Toast.makeText(MainActivity.this, "Please enter an item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateButton.setOnClickListener(v -> {
            String editedItem = newItemEditText.getText().toString();
            if (!editedItem.isEmpty()) {
                todoList.set(editItemPosition, editedItem);
                todoAdapter.notifyDataSetChanged();
                newItemEditText.setText("");
                isEditing = false;
                editItemPosition = -1;
                updateButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                addButton.setVisibility(View.VISIBLE);
                saveItems();
                Toast.makeText(MainActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please enter an item", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            todoList.remove(editItemPosition);
            todoAdapter.notifyDataSetChanged();
            newItemEditText.setText("");
            isEditing = false;
            editItemPosition = -1;
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            saveItems();
            Toast.makeText(MainActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
        });

        todoListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = filteredTodoList.get(position);
            newItemEditText.setText(selectedItem);
            isEditing = true;
            editItemPosition = todoList.indexOf(selectedItem);
            updateButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //call filter based on char sequence
                filterItems(s.toString());
                todoAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
        // Load all items on sharedpref everytime app is opened
        loadItems();
    }
    private void loadItems() {
        Set<String> itemSet = sharedPreferences.getStringSet(TODO_LIST_KEY, null);
        if (itemSet != null) {
            todoList.clear();
            todoList.addAll(itemSet);
            filterItems(searchEditText.getText().toString());
            todoAdapter.notifyDataSetChanged();
        }
    }

    private void saveItems() {
        Set<String> itemSet = new HashSet<>(todoList);
        sharedPreferences.edit().putStringSet(TODO_LIST_KEY, itemSet).apply();
    }

    private void filterItems(String query) {
        filteredTodoList.clear();
        for (String item : todoList) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredTodoList.add(item);
            }
        }
    }
}

