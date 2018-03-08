package ru.danya02.learnfaces;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
    }
    public void start(View view){
        Intent toGame = new Intent(this, GameActivity.class);
        toGame.putExtra("questions",10);
        startActivity(toGame);
    }

    public void to_rtfs(View view) {
        Intent toFS = new Intent(Intent.ACTION_VIEW);
        toFS.setData(Uri.parse(getString(R.string.source_link)));
        startActivity(toFS);
    }
}
