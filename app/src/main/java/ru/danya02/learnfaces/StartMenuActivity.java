package ru.danya02.learnfaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        final Button start = findViewById(R.id.b_start);
        start.setText(R.string.start_button_text);
        start.setOnClickListener(this::start);
        final Button rtfs = findViewById(R.id.b_rtfs);
        rtfs.setText(R.string.rtfs_button_text);
        rtfs.setOnClickListener(this::toRtfs);
        final Button dataview = findViewById(R.id.b_dataview);
        dataview.setText(R.string.b_dataview_text);
        dataview.setOnClickListener(this::toDataview);
        final Button settings = findViewById(R.id.b_settings);
        settings.setOnClickListener((view) -> Toast.makeText(this, "NotImplemented", Toast.LENGTH_LONG).show());
    }

    public void start(View view) {
        Intent toGame = new Intent(this, GameActivity.class);
        Integer qn = 10;
        toGame.putExtra("questions", qn);
        toGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(toGame);
    }

    public void toRtfs(View view) {
        Intent toFS = new Intent(Intent.ACTION_VIEW);
        toFS.setData(Uri.parse(getString(R.string.source_link)));
        startActivity(toFS);
    }

    public void toDataview(View view) {
        Intent to_data = new Intent(this, DatabaseViewActivity.class);
        startActivity(to_data);
    }

}
