package ru.danya02.learnfaces;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent origin = getIntent();
        Integer total = origin.getIntExtra("total", 10);
        Integer good = origin.getIntExtra("good", 0);
        Integer skipped = origin.getIntExtra("skip", 10);
        Integer bad = origin.getIntExtra("bad", 0);

        final RatingBar ratingBar = findViewById(R.id.rating);
        ratingBar.setMax(total);
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(good/total);
        int textSizeLabel=16;
        TextView countLabel = findViewById(R.id.count_label);
        countLabel.setText(String.format(getText(R.string.results_total).toString(),total.toString()));
        TextView goodLabel = findViewById(R.id.good_label);
        goodLabel.setText(R.string.good_label);
        goodLabel.setTextSize(textSizeLabel);
        TextView skipLabel = findViewById(R.id.skip_label);
        skipLabel.setText(R.string.skip_label);
        skipLabel.setTextSize(textSizeLabel);
        TextView badLabel = findViewById(R.id.bad_label);
        badLabel.setText(R.string.bad_label);
        badLabel.setTextSize(textSizeLabel);


        int textSize=48;
        TextView goodCount = findViewById(R.id.good_count);
        goodCount.setTextColor(getResources().getColor(R.color.colorGood));
        goodCount.setTextSize(textSize);
        goodCount.setText(good.toString());

        TextView skipCount = findViewById(R.id.skip_count);
        skipCount.setTextColor(getResources().getColor(R.color.colorSkip));
        skipCount.setTextSize(textSize);
        skipCount.setText(skipped.toString());

        TextView badCount = findViewById(R.id.bad_count);
        badCount.setTextColor(getResources().getColor(R.color.colorBad));
        badCount.setTextSize(textSize);
        badCount.setText(bad.toString());

        Button tryAgain = findViewById(R.id.try_again);
        tryAgain.setText(R.string.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game();
            }
        });
    }
    void game(){
        Intent toGame = new Intent(this, GameActivity.class);
        toGame.putExtra("questions",10);
        startActivity(toGame);
    }
}
