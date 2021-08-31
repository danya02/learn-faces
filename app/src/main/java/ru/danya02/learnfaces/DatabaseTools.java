package ru.danya02.learnfaces;


import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class DatabaseTools {
    public enum databaseStatus {OK, BAD_URI, NOT_CONNECTING, BAD_JSON}

    public static databaseStatus databaseAddressCheck(String address) {
        URL addr;
        try {
            addr = new URL(address + "database.json");
        } catch (MalformedURLException e) {
            Log.e("addressTester", "The address fails the URI test.", e);
            return databaseStatus.BAD_URI;
        }
        URLConnection connection;
        try {
            connection = addr.openConnection();
        } catch (IOException e) {
            Log.e("addressTester", "The address fails the connection test.", e);
            return databaseStatus.NOT_CONNECTING;
        }


        String dataFromNet;
        InputStream in;
        try {
            in = new BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            Log.e("addressTester", "The address fails the streaming test.", e);
            return databaseStatus.NOT_CONNECTING;
        }
        Scanner scanner = new Scanner(in);
        StringBuilder fromNet = new StringBuilder();
        while (scanner.hasNext()) {
            fromNet.append(" ").append(scanner.next());
        }
        dataFromNet = fromNet.toString();
        try {
            JSONObject object = new JSONObject(dataFromNet);
            object.getJSONArray("userlist");
        } catch (JSONException e) {
            Log.e("addressTester", "The address fails the JSON test.", e);
            return databaseStatus.BAD_JSON;
        }


        Log.i("addressTester", "The address passes all tests.");

        return databaseStatus.OK;

    }

    public static boolean databaseOnDiskOutdated(URL url, Context context) {
        String dataFromNet, dataFromFile = null;
        HttpURLConnection urlConnection;
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
                fromNet.append(" ").append(scanner.next());
            }
            dataFromNet = fromNet.toString();
        } catch (Exception e) {
            Log.e("testNeedsUpdate", "Problem while reading from connection.", e);
            return false;
        } finally {
            urlConnection.disconnect();
        }
        InputStream inputStream;
        try {
            inputStream = context.openFileInput("data.json");
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
                    fromFile.append(" ").append(receiveString).append(" ");
                }
                dataFromFile = fromFile.toString();
            } catch (IOException e) {
                Log.e("testNeedsUpdate", "Error while reading file.", e);
                return true;
            }
        }
        JSONObject jObjectFromFile, jObjectFromNet;
        try {
            jObjectFromFile = new JSONObject(dataFromFile);
            jObjectFromNet = new JSONObject(dataFromNet);
        } catch (JSONException e) {
            Log.e("testNeedsUpdate", "Invalid JSON.", e);
            return true;
        }
        return !(jObjectFromFile.toString().equals(jObjectFromNet.toString()));
    }
}
