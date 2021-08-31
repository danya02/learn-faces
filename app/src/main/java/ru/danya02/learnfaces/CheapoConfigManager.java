package ru.danya02.learnfaces;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

public class CheapoConfigManager {
    // Because I can't be arsed to learn how to access the system settings database.

    private final Context context;

    public CheapoConfigManager(Context c) {
        context = c;
    }

    public String getDataField(String param) {
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(param + ".parameter");
        } catch (FileNotFoundException e) {
            Log.e("cheapoConfigManager", "File not found: " + param + ".parameter", e);
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString;
        StringBuilder fromFile = new StringBuilder();
        String data = null;
        try {
            while ((receiveString = bufferedReader.readLine()) != null) {
                fromFile.append(" ").append(receiveString).append(" ");
            }
            data = fromFile.toString().trim();
        } catch (IOException e) {
            Log.e("cheapoConfigManager", "Error while reading from file.", e);
        }
        return data;
    }

    public void setDataField(String param, String data) throws IOException {
        try {
            Writer w = new FileWriter(new File(context.getFilesDir(), param + ".parameter").getAbsolutePath());
            w.write(data);
            w.close();
        } catch (IOException e) {
            Log.e("cheapoConfigManager", "Error while writing file.", e);
            throw e;
        }
    }
}
