package ru.danya02.learnfaces;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

public class DatabaseViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_view);
        RecyclerView v = findViewById(R.id.database_recyclerview);
        v.setAdapter(new DatabaseAdapter());
    }
}
