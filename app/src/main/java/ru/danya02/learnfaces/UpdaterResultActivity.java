package ru.danya02.learnfaces;

import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdaterResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater_result);
        Intent i = getIntent();
        String result = i.getStringExtra("result");
        if (result == null) {
            goodSetup();
        } else {
            badSetup(result);
        }
    }

    void goodSetup() {
        ImageView iv = findViewById(R.id.update_result_image_view);
        iv.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_launcher_background)); //TODO: set actual drawables.
        TextView t = findViewById(R.id.update_result);
        t.setText(R.string.update_no_error);
        Button b = findViewById(R.id.b_continue_from_update);
        b.setText(R.string.b_continue_from_update_text);
        b.setOnClickListener(v -> continueWork());
    }

    void badSetup(String result) {
        ImageView iv = findViewById(R.id.update_result_image_view);
        iv.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_launcher_background)); //TODO: set actual drawables.
        TextView t = findViewById(R.id.update_result);
        t.setText(String.format(getString(R.string.update_error_formattable), result));
        Button b = findViewById(R.id.b_continue_from_update);
        b.setText(R.string.update_try_again);
        b.setOnClickListener(v -> updateAgain());
    }

    void continueWork() {
        Intent i = new Intent(this, StartMenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    void updateAgain() {
        Intent i = new Intent(this, UpdaterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

}
