package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE =20;
    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("To Do list");

        btnAdd=findViewById(R.id.button3);
        etItem=findViewById(R.id.editTextTextPersonName2);
        rvItems=findViewById(R.id.rvitems);


        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){

            @Override
            public void onItemLongClickListener(int position) {
                //Delete item
                items.remove(position);
                //Notify adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item Removed",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener=new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Log.d("MainActivity", "Single Click at "+position);
                //create new activity
                Intent i= new Intent(MainActivity.this,EditActivity.class);
                //pass that data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter=new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem=etItem.getText().toString();
                //Add item
                items.add(todoItem);
                //Tell adapter that an item was inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item Added",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }
    //handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Retrieve updated text
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //Extract original position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update model
            items.set(position, itemText);

            itemsAdapter.notifyItemChanged(position);

            saveItems();

            Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(),"data.txt");
    }

    //Loads items
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items");
            items=new ArrayList<>();
        }
    }


    //Writes items
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items");
        }
    }
}