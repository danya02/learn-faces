package ru.danya02.learnfaces;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class GameActivity extends AppCompatActivity {

    enum buttons {BUTTON1, BUTTON2, BUTTON3, BUTTON4}

    Random r = new Random();
    ArrayList<String> ids = new ArrayList<>();
    HashMap<String, String> names = new HashMap<>();
    buttons correctAnswer;
    Integer correctAnswers = 0, wrongAnswers = 0, skippedQuestions = 0;

    boolean needsUpdating() {
        URL url;
        String dataFromNet, dataFromFile = null;
        try {
            url = new URL(getString(R.string.database_link));
        } catch (MalformedURLException e) {
            Log.wtf("testNeedsUpdate", "Error of URL in resources?!", e);
            return false;
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e("testNeedsUpdate", "Problem while opening connection.", e);
            return false;
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(in);
            StringBuilder fromNet = new StringBuilder();
            while (scanner.hasNext()) {
                fromNet.append(scanner.next());
            }
            dataFromNet = fromNet.toString();
        } catch (Exception e) {
            Log.e("testNeedsUpdate", "Problem while reading from connection.", e);
            return false;
        } finally {
            urlConnection.disconnect();
        }
        InputStream inputStream = null;
        try {
            inputStream = getApplicationContext().openFileInput("data.json");
        } catch (FileNotFoundException e) {
            Log.e("testNeedsUpdate", "Error while opening file.", e);
            return true;
        }

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder fromFile = new StringBuilder();

            try {
                while ((receiveString = bufferedReader.readLine()) != null) {
                    fromFile.append(receiveString);
                }
                dataFromFile = fromFile.toString();
            } catch (IOException e) {
                Log.e("testNeedsUpdate", "Error while reading file.", e);
                return true;
            }
        }
        return !Objects.equals(dataFromFile, dataFromNet);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        getContactIndex();
        ProgressBar p = findViewById(R.id.progressBar);
        Intent origin = getIntent();
        p.setMax(origin.getIntExtra("questions", 10));
        ImageButton b1 = findViewById(R.id.b1);
        ImageButton b2 = findViewById(R.id.b2);
        ImageButton b3 = findViewById(R.id.b3);
        ImageButton b4 = findViewById(R.id.b4);
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
        try {
            generateQuestion();
        } catch (IllegalStateException e) {
            Toast t = Toast.makeText(getApplicationContext(), R.string.no_images_toast, Toast.LENGTH_LONG);
            t.show();
            finish();
        }
    }

    @Deprecated
    private void getContactIndex() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        assert phones != null;
        while (phones.moveToNext()) {
            ids.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            names.put(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)), phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        }
        phones.close();
    }

    public void generateQuestion() throws IllegalStateException {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        ImageButton[] imageButtons = {findViewById(R.id.b1), findViewById(R.id.b2), findViewById(R.id.b3), findViewById(R.id.b4)};
        ImageButton correctButton = imageButtons[r.nextInt(imageButtons.length)];
        ArrayList<ImageButton> wrongButtons = new ArrayList<>();
        switch (correctButton.getId()) {
            case R.id.b1:
                Log.d("button", "The correct button is b1.");
                correctAnswer = buttons.BUTTON1;
                wrongButtons.add((ImageButton) findViewById(R.id.b2));
                wrongButtons.add((ImageButton) findViewById(R.id.b3));
                wrongButtons.add((ImageButton) findViewById(R.id.b4));
                break;
            case R.id.b2:
                Log.d("button", "The correct button is b2.");
                correctAnswer = buttons.BUTTON2;
                wrongButtons.add((ImageButton) findViewById(R.id.b1));
                wrongButtons.add((ImageButton) findViewById(R.id.b3));
                wrongButtons.add((ImageButton) findViewById(R.id.b4));
                break;
            case R.id.b3:
                Log.d("button", "The correct button is b3.");
                correctAnswer = buttons.BUTTON3;
                wrongButtons.add((ImageButton) findViewById(R.id.b1));
                wrongButtons.add((ImageButton) findViewById(R.id.b2));
                wrongButtons.add((ImageButton) findViewById(R.id.b4));
                break;
            case R.id.b4:
                Log.d("button", "The correct button is b4.");
                correctAnswer = buttons.BUTTON4;
                wrongButtons.add((ImageButton) findViewById(R.id.b1));
                wrongButtons.add((ImageButton) findViewById(R.id.b2));
                wrongButtons.add((ImageButton) findViewById(R.id.b3));
                break;
        }

        Bitmap photo;
        InputStream inputStream = null;
        String targetID = null;
        ArrayList<String> selectedTargets = new ArrayList<>();
        while (inputStream == null) {
            targetID = ids.get(r.nextInt(ids.size()));
            inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(targetID)), true);
        }

        selectedTargets.add(targetID);
        photo = BitmapFactory.decodeStream(inputStream).copy(Bitmap.Config.ARGB_8888, true);
        photo = Bitmap.createScaledBitmap(photo, Math.min(photo.getWidth(), width / 2), Math.min(photo.getHeight(), height / 2), false); /* FIXME: pic gets distorted */
        String name = names.get(targetID);
        correctButton.setImageBitmap(photo);
        for (int i = 0; i < wrongButtons.size(); i++) {
            Bitmap photo1;
            String targetID1;
            InputStream inputStream1 = null;
            int counter = 0;
            while (inputStream1 == null) {
                counter += 1;
                if (counter >= 1024) {
                    throw new IllegalStateException("Too many loops!");
                }
                inputStream1 = null;
                targetID1 = ids.get(r.nextInt(ids.size()));
                if (!selectedTargets.contains(targetID1)) {
                    /* FIXME: won't select a contact with image if contact doesn't contain a phone number. */
                    inputStream1 = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(targetID1)), true);

                    selectedTargets.add(targetID1);
                    if (inputStream1 != null) {
                        photo1 = BitmapFactory.decodeStream(inputStream1).copy(Bitmap.Config.ARGB_8888, true);
                        photo1 = Bitmap.createScaledBitmap(photo1, Math.min(photo1.getWidth(), width / 2), Math.min(photo1.getHeight(), height / 2), false);
                        wrongButtons.get(i).setImageBitmap(photo1);
                    }

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
