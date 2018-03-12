package ru.danya02.learnfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UpdaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
    }

    boolean startUpdate(View v) {
        URL url;
        String dataFromNet, dataFromFile = null;
        try {
            url = new URL(getString(R.string.database_link));
        } catch (MalformedURLException e) {
            Log.wtf("updateData", "Error of URL in resources?!", e);
            return false;
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e("updateData", "Problem while opening connection.", e);
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
            Log.e("updateData", "Problem while reading from connection.", e);
            return false;
        } finally {
            urlConnection.disconnect();
        }

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("data.json", Context.MODE_PRIVATE);
            outputStream.write(dataFromNet.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("updateData", "Problem while writing to file.", e);
            return false;
        }
        //TODO: Add downloading of images.
        return true;
    }
}
