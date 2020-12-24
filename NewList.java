package com.example.randomchoicegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

public class NewList extends AppCompatActivity {
    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;
    EditText addText;
    Button addButton;
    Button genButton;
    Button saveButton;
    Button selectAllButton;
    Button deleteButton;
    Button backButton;
    Button clearListButton;
    TextView generated;
    ListView lv;
    Random r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        lv = findViewById(R.id.listView);
        addText = findViewById(R.id.addText);
        addButton = findViewById(R.id.addButton);
        genButton = findViewById(R.id.genButton);
        saveButton = findViewById(R.id.saveButton);
        generated = findViewById(R.id.generated);
        selectAllButton = findViewById(R.id.selectAllButton);
        deleteButton = findViewById(R.id.deleteButton);
        clearListButton = findViewById(R.id.clearListButton);
        backButton = findViewById(R.id.backButton);
        loadData();
        //itemList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(NewList.this, android.R.layout.simple_list_item_multiple_choice,itemList);
        r = new Random();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewList.this);
                builder.setMessage("Are you sure you want to go back without saving? All changes made will be lost.");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        NewList.super.onBackPressed();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemList.isEmpty()){
                    Toast.makeText(NewList.this, "List is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (int i = 0; i < lv.getChildCount(); i++) {
                        lv.setItemChecked(i, true);
                    }
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Toast.makeText(NewList.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addText.getText().toString().matches("")) {
                    Toast.makeText(NewList.this, "You did not enter anything", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    itemList.add(addText.getText().toString());
                    addText.setText(null);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemList.isEmpty()){
                    Toast.makeText(NewList.this, "List is empty", Toast.LENGTH_SHORT).show();
                }else {
                    SparseBooleanArray positionChecker = lv.getCheckedItemPositions();
                    int count = lv.getCount();
                    for (int item = count - 1; item >= 0; --item) {
                        if (positionChecker.get(item)) {
                            adapter.remove(itemList.get(item));
                            Toast.makeText(NewList.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    positionChecker.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemList.isEmpty()){
                    Toast.makeText(NewList.this, "List is empty", Toast.LENGTH_SHORT).show();
                }else {
                    int count = lv.getCount();
                    for (int item = count - 1; item >= 0; --item) {
                        adapter.remove(itemList.get(item));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray positionChecker = lv.getCheckedItemPositions();
                int count = lv.getCount();
                for(int item = count-1; item>=0; --item){
                    if(positionChecker.get(item)){
                        adapter.remove(itemList.get(item));
                        Toast.makeText(NewList.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
                positionChecker.clear();
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        genButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemList.isEmpty()){
                    Toast.makeText(NewList.this, "List is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    generated.setText(itemList.get(r.nextInt(itemList.size())));
                }
            }
        });

        addButton.setOnClickListener(addListener);
        lv.setAdapter(adapter);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        itemList = gson.fromJson(json, type);

        if(itemList == null){
            itemList = new ArrayList<>();
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NewList.this);
        builder.setMessage("Are you sure you want to go back without saving? All changes made will be lost.");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                NewList.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        }
}
