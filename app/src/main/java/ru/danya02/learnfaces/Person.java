package ru.danya02.learnfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Person {
    public String name, main_pic;
    public List<String> alt_pics;

    public Person(String name, String main_pic, List<String> alt_pics) {
        this.name = name;
        this.main_pic = main_pic;
        this.alt_pics = alt_pics;
    }

    public Person() {

    }

    // the superclass -- Object -- has toString() annotated as @RecentlyNotNull
    // this is the same as @NotNull, so we need to annotate it as such.
    // see https://stackoverflow.com/a/53059367/5936187
    @NonNull
    public String toString() {
        return name;
    }


    static public ArrayList<Person> loadData(Context context) throws JSONException, FileNotFoundException {
        ArrayList<Person> people = new ArrayList<>();
        FileInputStream file;
        try {
            file = context.openFileInput("data.json");
        } catch (FileNotFoundException e) {
            Log.e("loadData", "Not found database; telling to update...", e);
            throw e;
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
            Person person = new Person();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            person.name = (String) jsonObject.get("name");
            person.main_pic = (String) jsonObject.get("main_pic");
            JSONArray a = jsonObject.getJSONArray("alt_pics");
            ArrayList<String> s = new ArrayList<>();
            for (int j = 0; j < a.length(); j++) {
                s.add(a.getString(j));
            }
            person.alt_pics = s;
            people.add(person);
        }
        return people;
    }
}
