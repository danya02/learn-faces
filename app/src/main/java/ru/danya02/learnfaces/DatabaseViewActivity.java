package ru.danya02.learnfaces;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
            Toast.makeText(this, R.string.no_database_need_update, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(this, R.string.error_in_json, Toast.LENGTH_LONG).show();
        }
    }
}
