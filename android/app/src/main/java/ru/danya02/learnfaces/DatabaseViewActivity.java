package ru.danya02.learnfaces;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileNotFoundException;

public class DatabaseViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_view);
        RecyclerView v = findViewById(R.id.database_recyclerview);
        v.setLayoutManager(new LinearLayoutManager(this));
        try {
            v.setAdapter(new DatabaseAdapter(this));
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "No database file found; play to update.", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(this, "Unexpected error in JSON file.", Toast.LENGTH_LONG).show();
        }
    }
}
