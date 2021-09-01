package ru.danya02.learnfaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameActivity extends AppCompatActivity {

    enum buttons {BUTTON1, BUTTON2, BUTTON3, BUTTON4}

    Random r = new Random();
    ArrayList<Person> json_arr = new ArrayList<>();
    buttons correctAnswer;
    Integer correctAnswers = 0, wrongAnswers = 0, skippedQuestions = 0;
    ArrayList<Answer> answers = new ArrayList<>();
    String correctName, correctPath;

    Target<Drawable> Button1Target, Button2Target, Button3Target, Button4Target;

    ProgressDialog dialog;
    GameActivity activity;

    public void showDialog() {
        runOnUiThread(() -> {
            dialog = new ProgressDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.toast_testing_update));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        });
    }

    public void hideDialog() {
        runOnUiThread(() -> dialog.dismiss());
    }

    private void testNeedsUpdating() {
        showDialog();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Runnable updateRequired = () -> {
                handler.post(GameActivity.this::hideDialog);
                updateData();
            };
            Runnable noUpdateNeeded = () -> {
                handler.post(GameActivity.this::hideDialog);
                generateQuestionWrapper(true);
            };


            URL url;
            try {
                url = new URL(new CheapoConfigManager(getApplicationContext()).getDataField("databaseUri") + "database.json");
            } catch (MalformedURLException e) {
                Log.wtf("testNeedsUpdate", "Error in tested URL?!", e);
                noUpdateNeeded.run();
                return;
            }
            if (DatabaseTools.databaseOnDiskOutdated(url, getApplicationContext()))
                updateRequired.run();
            else noUpdateNeeded.run();
        });
    }

    private void updateData() {
        Toast.makeText(this, R.string.toast_obligatory_update, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, UpdaterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_main);
        try {
            json_arr = Person.loadData(getApplicationContext());
            if (json_arr.size() == 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            Log.i("loadData", "There was an error, so updating data.", e);
            updateData();
        }
        ProgressBar p = findViewById(R.id.progressBar);
        Intent origin = getIntent();
        p.setMax(origin.getIntExtra("questions", 10));
        ImageButton b1 = findViewById(R.id.b1);
        ImageButton b2 = findViewById(R.id.b2);
        ImageButton b3 = findViewById(R.id.b3);
        ImageButton b4 = findViewById(R.id.b4);
        Button1Target = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                ((ImageButton) findViewById(R.id.b1)).setImageDrawable(resource);
            }
        };
        Button2Target = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                ((ImageButton) findViewById(R.id.b2)).setImageDrawable(resource);
            }
        };
        Button3Target = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                ((ImageButton) findViewById(R.id.b3)).setImageDrawable(resource);
            }
        };
        Button4Target = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                ((ImageButton) findViewById(R.id.b4)).setImageDrawable(resource);
            }
        };

        Button skip = findViewById(R.id.skip);
        b1.setOnClickListener(v -> onButtonPress(buttons.BUTTON1));
        b2.setOnClickListener(v -> onButtonPress(buttons.BUTTON2));
        b3.setOnClickListener(v -> onButtonPress(buttons.BUTTON3));
        b4.setOnClickListener(v -> onButtonPress(buttons.BUTTON4));
        skip.setOnClickListener(v -> onSkip());
                testNeedsUpdating();
    }

    public void generateQuestion() throws IllegalStateException {
        int[] imageButtonNumbers = {1, 2, 3, 4};
        int correctButtonNumber = imageButtonNumbers[r.nextInt(imageButtonNumbers.length)];
        final ArrayList<Target<Drawable>> wrongButtonTargets = new ArrayList<>();
        final Target<Drawable> correctButtonTarget;
        switch (correctButtonNumber) {
            case 1:
                Log.d("button", "The correct button is b1.");
                correctAnswer = buttons.BUTTON1;
                correctButtonTarget = Button1Target;
                wrongButtonTargets.add(Button2Target);
                wrongButtonTargets.add(Button3Target);
                wrongButtonTargets.add(Button4Target);
                break;
            case 2:
                Log.d("button", "The correct button is b2.");
                correctAnswer = buttons.BUTTON2;
                correctButtonTarget = Button2Target;
                wrongButtonTargets.add(Button1Target);
                wrongButtonTargets.add(Button3Target);
                wrongButtonTargets.add(Button4Target);
                break;
            case 3:
                Log.d("button", "The correct button is b3.");
                correctAnswer = buttons.BUTTON3;
                correctButtonTarget = Button3Target;
                wrongButtonTargets.add(Button1Target);
                wrongButtonTargets.add(Button2Target);
                wrongButtonTargets.add(Button4Target);
                break;
            case 4:
                Log.d("button", "The correct button is b4.");
                correctAnswer = buttons.BUTTON4;
                correctButtonTarget = Button4Target;
                wrongButtonTargets.add(Button1Target);
                wrongButtonTargets.add(Button2Target);
                wrongButtonTargets.add(Button3Target);
                break;
            default:
                Log.d("button", "The correct button is b1.");
                Log.wtf("button", "The correct button number check fell through all possible values and got " + correctButtonNumber + "?!");
                correctAnswer = buttons.BUTTON1;
                correctButtonTarget = Button1Target;
                wrongButtonTargets.add(Button2Target);
                wrongButtonTargets.add(Button3Target);
                wrongButtonTargets.add(Button4Target);
                break;
        }

        ArrayList<Person> selectedTargets = new ArrayList<>();
        selectedTargets.add(json_arr.get(r.nextInt(json_arr.size())));
        final String storage = getFilesDir().getPath();
        correctPath = storage + "/" + selectedTargets.get(0).main_pic;
        runOnUiThread(() -> {
            Glide.with(getApplicationContext()).clear(correctButtonTarget);
            Glide.with(getApplicationContext()).load(correctPath).into(correctButtonTarget);

        });
        final String name = String.valueOf(selectedTargets.get(0).name);
        for (int i = 0; i < wrongButtonTargets.size(); i++) {
            Person target;
            int counter = 0;
            boolean selected = false;
            while (!selected) {
                counter += 1;
                if (counter >= 1024) {
                    throw new IllegalStateException("Too many loops!");
                }
                target = json_arr.get(r.nextInt(json_arr.size()));
                if (!selectedTargets.contains(target)) {
                    selected = true;
                    selectedTargets.add(target);
                    final Person finalTarget = target;
                    final int finalI = i;
                    runOnUiThread(() -> {

                        Glide.with(getApplicationContext()).clear(wrongButtonTargets.get(finalI));
                        Glide.with(getApplicationContext()).load(storage + "/" + finalTarget.main_pic).into(wrongButtonTargets.get(finalI));
                    });
                }

            }
            Log.d("pics", "generateQuestion: Selected pic for wrong button " + i);
        }
        final TextView t = findViewById(R.id.question);
        runOnUiThread(() -> t.setText(String.format(getResources().getText(R.string.default_question).toString(), name)));
        correctName = name;
    }

    private void generateQuestionWrapper(boolean exit) {
        try {
            generateQuestion();
        } catch (IllegalStateException e) {
            Toast t = Toast.makeText(getApplicationContext(), R.string.no_images_toast, Toast.LENGTH_LONG);
            t.show();
            if (exit) {
                finish();
            }
        }
    }

    public void onButtonPress(buttons b) {
        Answer answer = new Answer();
        if (b == correctAnswer) {
            correctAnswers += 1;
            answer.state = Answer.states.CORRECT;
        } else {
            wrongAnswers += 1;
            answer.state = Answer.states.INCORRECT;
        }
        ProgressBar p = findViewById(R.id.progressBar);
        p.setProgress(p.getProgress() + 1);
        answer.name = correctName;
        answer.path = correctPath;
        answers.add(answer);
        if (p.getProgress() != p.getMax()) {
            try {
                generateQuestion();
            } catch (IllegalStateException e) {
                Toast t = Toast.makeText(getApplicationContext(), R.string.no_images_toast, Toast.LENGTH_LONG);
                t.show();
            }
        } else {
            results();
        }
    }

    public void onSkip() {
        Answer answer = new Answer();
        answer.state = Answer.states.SKIPPED;
        skippedQuestions += 1;
        answer.name = correctName;
        answer.path = correctPath;
        answers.add(answer);
        ProgressBar p = findViewById(R.id.progressBar);
        p.setProgress(p.getProgress() + 1);
        if (p.getProgress() != p.getMax()) {
            generateQuestionWrapper(false);
        } else {
            results();
        }

    }

    void results() {
        ProgressBar p = findViewById(R.id.progressBar);
        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra("total", p.getMax());
        results.putExtra("good", correctAnswers);
        results.putExtra("skip", skippedQuestions);
        results.putExtra("bad", wrongAnswers);
        Bundle args = new Bundle();
        args.putSerializable("answers", answers);
        results.putExtra("answers", args);
        results.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(results);
        finish();
    }
}
