package ru.danya02.learnfaces;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GameActivity extends AppCompatActivity {

    enum buttons {BUTTON1, BUTTON2, BUTTON3, BUTTON4}

    Random r = new Random();
    ArrayList<HashMap<String, ArrayList<String>>> json_arr = new ArrayList<>();
    buttons correctAnswer;
    Integer correctAnswers = 0, wrongAnswers = 0, skippedQuestions = 0;

    Target<Drawable> Button1Target, Button2Target, Button3Target, Button4Target;

    class TestJSONChanged extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            String dataFromNet, dataFromFile = null;
            try {
                url = new URL(getString(R.string.database_link));
            } catch (MalformedURLException e) {
                Log.wtf("testNeedsUpdate", "Error of URL in resources?!", e);
                leaveTest(false);
                return null;
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.e("testNeedsUpdate", "Problem while opening connection.", e);
                leaveTest(false);
                return null;
            }
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Scanner scanner = new Scanner(in);
                StringBuilder fromNet = new StringBuilder();
                while (scanner.hasNext()) {
                    fromNet.append(" ").append(scanner.next());
                }
                dataFromNet = fromNet.toString();
            } catch (Exception e) {
                Log.e("testNeedsUpdate", "Problem while reading from connection.", e);
                leaveTest(false);
                return null;
            } finally {
                urlConnection.disconnect();
            }
            InputStream inputStream;
            try {
                inputStream = getApplicationContext().openFileInput("data.json");
            } catch (FileNotFoundException e) {
                Log.e("testNeedsUpdate", "Error while opening file.", e);
                leaveTest(true);
                return null;
            }

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder fromFile = new StringBuilder();

                try {
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        fromFile.append(" ").append(receiveString).append(" ");
                    }
                    dataFromFile = fromFile.toString();
                } catch (IOException e) {
                    Log.e("testNeedsUpdate", "Error while reading file.", e);
                    leaveTest(true);
                    return null;
                }
            }
            JSONObject jObjectFromFile, jObjectFromNet;
            try {
                jObjectFromFile = new JSONObject(dataFromFile);
                jObjectFromNet = new JSONObject(dataFromNet);
            } catch (JSONException e) {
                Log.wtf("testNeedsUpdate", "Invalid trusted JSON?!", e);
                leaveTest(true);
                return null;
            }
            leaveTest(!(jObjectFromFile.toString().equals(jObjectFromNet.toString())));
            return null;
        }

        private void leaveTest(boolean update) {
            if (update) leaveToUpdate = true;
            else runMain = true;
        }
    }

    private void testNeedsUpdating() {
        TestJSONChanged a = new TestJSONChanged();
        a.execute();
    }

    private void updateData() {
        Intent i = new Intent(this, UpdaterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() throws JSONException {
        FileInputStream file;
        try {
            file = openFileInput("data.json");
        } catch (FileNotFoundException e) {
            Log.e("loadData", "Not found database; skipping to updating...", e);
            updateData();
            return;
        }
        StringBuilder text = new StringBuilder();

        Scanner r = new Scanner(file);

        while (r.hasNext()) {
            text.append(r.nextLine());
        }
        r.close();
        String json = text.toString();
        JSONObject jObject;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.wtf("loadData", "Cannot parse trusted JSON?!", e);
            throw e;
        }
        JSONArray jsonArray;
        try {
            jsonArray = jObject.getJSONArray("userlist");
        } catch (JSONException e) {
            Log.wtf("loadData", "Cannot find `userlist` in JSON?!", e);
            throw e;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            HashMap<String, ArrayList<String>> s = new HashMap<>();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ArrayList<String> ss = new ArrayList<>();
            ss.add(String.valueOf(jsonObject.get("name")));
            s.put("name", ss);
            ss = new ArrayList<>();
            ss.add(String.valueOf(jsonObject.get("main_pic")));
            s.put("main_pic", ss);
            json_arr.add(s);
        }
    }

    boolean leaveToUpdate = false;
    boolean runMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        json_arr = new ArrayList<>();
        testNeedsUpdating();
        setContentView(R.layout.activity_main);
        try {
            loadData();
        } catch (JSONException e) {
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
        b1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onButtonPress(buttons.BUTTON1);
            }
        });
        b2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onButtonPress(buttons.BUTTON2);
            }
        });
        b3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onButtonPress(buttons.BUTTON3);
            }
        });
        b4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                onButtonPress(buttons.BUTTON4);
            }
        });
        skip.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSkip();
            }
        });
        while (!leaveToUpdate && !runMain) {
        }
        if (leaveToUpdate) updateData();
        else {

            try {
                generateQuestion();
            } catch (IllegalStateException e) {
                Toast t = Toast.makeText(getApplicationContext(), R.string.no_images_toast, Toast.LENGTH_LONG);
                t.show();
                finish();
            }
        }
    }

    public void generateQuestion() throws IllegalStateException {
        int[] imageButtonNumbers = {1, 2, 3, 4};
        int correctButtonNumber = imageButtonNumbers[r.nextInt(imageButtonNumbers.length)];
        ArrayList<Target<Drawable>> wrongButtonTargets = new ArrayList<>();
        Target<Drawable> correctButtonTarget;
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

        ArrayList<HashMap> selectedTargets = new ArrayList<>();
        selectedTargets.add(json_arr.get(r.nextInt(json_arr.size())));
        String storage = getFilesDir().getPath();
        Glide.with(this).clear(correctButtonTarget);
        Glide.with(this).load(storage + "/" + ((ArrayList) selectedTargets.get(0).get("main_pic")).get(0)).into(correctButtonTarget);
        String name = String.valueOf(selectedTargets.get(0).get("name"));
        name = name.substring(1, name.length() - 1);
        for (int i = 0; i < wrongButtonTargets.size(); i++) {
            HashMap target;
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
                    Glide.with(this).clear(wrongButtonTargets.get(i));
                    Glide.with(this).load(storage + "/" + ((ArrayList) target.get("main_pic")).get(0)).into(wrongButtonTargets.get(i));
                }

            }
            Log.d("pics", "generateQuestion: Selected pic for wrong button " + i);
        }
        TextView t = findViewById(R.id.question);
        t.setText(String.format(getResources().getText(R.string.default_question).toString(), name));
    }

    public void onButtonPress(buttons b) {
        if (b == correctAnswer) {
            correctAnswers += 1;
        } else {
            wrongAnswers += 1;
        }
        ProgressBar p = findViewById(R.id.progressBar);
        p.setProgress(p.getProgress() + 1);
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
        skippedQuestions += 1;
        ProgressBar p = findViewById(R.id.progressBar);
        p.setProgress(p.getProgress() + 1);
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

    void results() {
        ProgressBar p = findViewById(R.id.progressBar);
        Intent results = new Intent(this, ResultsActivity.class);
        results.putExtra("total", p.getMax());
        results.putExtra("good", correctAnswers);
        results.putExtra("skip", skippedQuestions);
        results.putExtra("bad", wrongAnswers);
        startActivity(results);
    }
}
