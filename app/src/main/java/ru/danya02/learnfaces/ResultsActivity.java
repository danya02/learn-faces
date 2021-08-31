package ru.danya02.learnfaces;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
        Bundle b = origin.getBundleExtra("answers");
        ArrayList<Answer> answers = (ArrayList<Answer>) b.getSerializable("answers");

        final RatingBar ratingBar = findViewById(R.id.rating);
        ratingBar.setMax(5);
        ratingBar.setIsIndicator(true);
        ratingBar.setRating((float) (((1.0 * good) / (1.0 * total)) * 5.0));

        EditText t = findViewById(R.id.question_num_results);
        t.setText(total.toString());

        int textSizeLabel = 16;
        TextView countLabel = findViewById(R.id.count_label);
        countLabel.setText(String.format(getText(R.string.results_total).toString(), total.toString()));
        TextView goodLabel = findViewById(R.id.good_label);
        goodLabel.setText(R.string.good_label);
        goodLabel.setTextSize(textSizeLabel);
        TextView skipLabel = findViewById(R.id.skip_label);
        skipLabel.setText(R.string.skip_label);
        skipLabel.setTextSize(textSizeLabel);
        TextView badLabel = findViewById(R.id.bad_label);
        badLabel.setText(R.string.bad_label);
        badLabel.setTextSize(textSizeLabel);


        int textSize = 48;
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
        RecyclerView recyclerView = findViewById(R.id.answer_list);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setAdapter(new AnswerListAdapter(answers, getResources()));
    }

    void game() {
        EditText t = findViewById(R.id.question_num_results);
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
}


class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.AnswerHolder> {

    private ArrayList<Answer> answers;
    private Resources resources;

    public AnswerListAdapter(ArrayList<Answer> answers, Resources resources) {
        this.answers = answers;
        this.resources = resources;
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    @NonNull
    @Override
    public AnswerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.results_item_list, viewGroup, false);
        return new AnswerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerHolder holder, int position) {
        Answer answer = answers.get(position);
        holder.personName.setText(answer.name);
        switch (answer.state) {
            case CORRECT:
                holder.personName.setTextColor(resources.getColor(R.color.colorGood));
                break;
            case INCORRECT:
                holder.personName.setTextColor(resources.getColor(R.color.colorBad));
                break;
            case SKIPPED:
                holder.personName.setTextColor(resources.getColor(R.color.colorSkip));
                break;
        }
        Glide.with(holder.itemView).load(answer.path).into(holder.personPic);

    }

    public static class AnswerHolder extends RecyclerView.ViewHolder {
        TextView personName;
        ImageView personPic;

        AnswerHolder(View itemView) {
            super(itemView);
            personPic = itemView.findViewById(R.id.results_answer_picture);
            personName = itemView.findViewById(R.id.results_answer_name);
        }
    }
}
