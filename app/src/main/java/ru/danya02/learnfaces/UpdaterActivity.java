package ru.danya02.learnfaces;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class UpdaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        ProgressBar b1 = findViewById(R.id.progressBarMain);
        ProgressBar b2 = findViewById(R.id.progressBarAux);
        TextView t = findViewById(R.id.textStatus);
        b1.setIndeterminate(true);
        b2.setIndeterminate(true);
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        t.setVisibility(View.GONE);
        Button b = findViewById(R.id.b_start_update);
        b.setText(R.string.update_start_text);
        b.setOnClickListener(v -> startUpdateWrapper());
    }

    void startUpdateWrapper() {
        try {
            startUpdate();
        } catch (Exception e) {
            leave(e.toString());
        }
    }

    void continueUpdateWrapper() {
        try {
            continueUpdate();
        } catch (Exception e) {
            leave(e.toString());
        }
    }

    ArrayList<String> paths;
    boolean left = false;


    // FIXME: replace with other concurrency mechanisms.
    class DownloadDatabase extends AsyncTask<String, String, String> {
        @SuppressLint("WrongThread") // re: line 108 and 110.
        // FIXME: figure out why this issues a warning, it worked before.
        @Override
        protected String doInBackground(String... strings) {
            int count;
            CheapoConfigManager configManager = new CheapoConfigManager(getApplication());

            String file = configManager.getDataField("databaseUri") + "database.json";
            try {
                URL url = new URL(file);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();

                int fileLength = urlConnection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                File f = new File(getFilesDir(), "data.json");
                assert f.createNewFile();
                OutputStream output = new FileOutputStream(String.format("%s/%s", getFilesDir(), "data.json"));

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileLength));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("updateData", String.format("Error while downloading file %s.", file), e);
                onPostExecute(e.toString());
            }
            onPostExecute(null);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            continueUpdateWrapper();
        }

        protected void onProgressUpdate(final String... progress) {
            runOnUiThread(() -> {
                ProgressBar p = findViewById(R.id.progressBarAux);
                p.setProgress(Integer.parseInt(progress[0]));
                p.setMax(100);
                p.setIndeterminate(false);

            });
        }
    }

    void startUpdate() {
        ProgressBar b1 = findViewById(R.id.progressBarAux);
        b1.setVisibility(View.VISIBLE);
        ProgressBar b2 = findViewById(R.id.progressBarMain);
        b2.setVisibility(View.VISIBLE);

        TextView t = findViewById(R.id.textStatus);
        b1.setIndeterminate(true);
        t.setVisibility(View.VISIBLE);
        t.setText(R.string.update_updating_index);
        DownloadDatabase d = new DownloadDatabase();
        d.execute();
    }

    void continueUpdate() throws JSONException, FileNotFoundException {
        FileInputStream file;
        final TextView t = findViewById(R.id.textStatus);
        try {
            file = openFileInput("data.json");
        } catch (FileNotFoundException e) {
            Log.wtf("updateData", "Not found file we just created?!", e);
            throw e;
        }
        runOnUiThread(() -> t.setText(R.string.update_json_parse));

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
            Log.e("updateData", "Cannot parse downloaded JSON.", e);
            throw e;
        }
        JSONArray jsonArray;
        try {
            jsonArray = jObject.getJSONArray("userlist");
        } catch (JSONException e) {
            Log.e("updateData", "Cannot find `userlist` in JSON.", e);
            throw e;
        }
        runOnUiThread(() -> t.setText(R.string.update_create_image_index));

        paths = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                String mainPic = oneObject.getString("main_pic");
                JSONArray altPics = oneObject.getJSONArray("alt_pics");
                ArrayList<String> altPicsPaths = new ArrayList<>();
                for (int j = 0; j < altPics.length(); j++) {
                    altPicsPaths.add(altPics.getString(j));
                }
                paths.add(mainPic);
                paths.addAll(altPicsPaths);
            } catch (JSONException e) {
                Log.wtf("updateData", "Error while parsing trusted JSON?!", e);
                throw e;
            }
        }
        downloadFromIndex(0);
    }

    // TODO: replace with other concurrency mechanisms.
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            final TextView t = findViewById(R.id.textStatus);
            final String file = strings[0];
            int index = Integer.parseInt(strings[1]);
            runOnUiThread(() -> t.setText(String.format(getString(R.string.update_downloading_formattable), file)));
            try {
                CheapoConfigManager configManager = new CheapoConfigManager(getApplicationContext());
                URL url = new URL(configManager.getDataField("databaseUri") + file);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();

                int fileLength = urlConnection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                File f = new File(getFilesDir(), file);
                assert Objects.requireNonNull(f.getParentFile()).mkdirs();
                assert f.createNewFile();
                OutputStream output = new FileOutputStream(String.format("%s/%s", getFilesDir(), file));

                byte[] data = new byte[1024];

                int count;
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileLength));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("updateData", String.format("Error while downloading file %s.", file), e);
                leave(e.toString());
            }
            onPostExecute(index);
            return null;
        }


        protected void onProgressUpdate(final String... progress) {
            runOnUiThread(() -> {
                ProgressBar p = findViewById(R.id.progressBarAux);
                p.setProgress(Integer.parseInt(progress[0]));
                p.setMax(100);
                p.setIndeterminate(false);
            });
        }

        protected void onPostExecute(int file_index) {
            downloadFromIndex(file_index + 1);
        }

    }

    void leave(String s) {
        if (!left) {
            left = true;
            Intent i = new Intent(this, UpdaterResultActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("result", s);
            finish();
            startActivity(i);
        }
    }

    void downloadFromIndex(final int index) {
        if (paths.size() <= index) {
            leave(null);
        } else {
            runOnUiThread(() -> {
                ProgressBar p = findViewById(R.id.progressBarMain);
                p.setMax(paths.size());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    p.setProgress(index, true);
                } else {
                    p.setProgress(index);
                }
                p.setIndeterminate(false);
            });

            DownloadFileFromURL d = new DownloadFileFromURL();
            d.execute(paths.get(index), String.valueOf(index));
        }
    }
}
