package ru.danya02.learnfaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);
        final Button start = findViewById(R.id.b_start);
        start.setText(R.string.start_button_text);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(v);
            }
        });
        final Button rtfs = findViewById(R.id.b_rtfs);
        rtfs.setText(R.string.rtfs_button_text);
        rtfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_rtfs(v);
            }
        });
        final Button dataview = findViewById(R.id.b_dataview);
        dataview.setText(R.string.b_dataview_text);
        dataview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_dataview(v);
            }
        });
    }

    public void start(View view) {
        EditText t = findViewById(R.id.question_num_startmenu);
        Intent toGame = new Intent(this, GameActivity.class);
        String qs = String.valueOf(t.getText());
        Integer qn = 10;
        try {
            qn = Integer.parseInt(qs);
        } catch (NumberFormatException e) {
            Log.wtf("startMenu", "Invalid number from number-only EditText?!", e);
        }
        toGame.putExtra("questions", qn);
        toGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(toGame);
    }

    public void to_rtfs(View view) {
        Intent toFS = new Intent(Intent.ACTION_VIEW);
        toFS.setData(Uri.parse(getString(R.string.source_link)));
        startActivity(toFS);
    }

    public void to_dataview(View view) {
        Intent to_data = new Intent(this, DatabaseViewActivity.class);
        startActivity(to_data);
    }
}
