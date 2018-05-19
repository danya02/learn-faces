package ru.danya02.learnfaces;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.PersonViewHolder> {


    private ArrayList<Person> persons;
    private Context context;


    public DatabaseAdapter(Context context) throws FileNotFoundException, JSONException {
        persons = Person.loadData(context);
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.database_view_item, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        holder.personName.setText(persons.get(position).name);
        ArrayList<String> pics = new ArrayList<>();
        pics.add(persons.get(position).main_pic);
        pics.addAll(persons.get(position).alt_pics);
        holder.personPics.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.personPics.setHasFixedSize(true);
        holder.personPics.setAdapter(new PicsAdapter(pics, context));
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        RecyclerView personPics;

        PersonViewHolder(View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.person_name);
            personPics = itemView.findViewById(R.id.person_pics);
        }
    }
}

class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.PicViewHolder> {


    private ArrayList<String> pics;
    private Context context;

    public PicsAdapter(ArrayList<String> pics, Context context) {
        this.context = context;
        this.pics = pics;
    }

    @Override
    public int getItemCount() {
        if (pics != null) return pics.size();
        else return 0;
    }

    @NonNull
    @Override
    public PicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.database_view_subitem, viewGroup, false);
        return new PicsAdapter.PicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, int position) {
        try {
            Glide.with(holder.itemView).load(context.getFilesDir().getPath() + "/" + pics.get(position)).into(holder.personPic);
        } catch (NullPointerException e) {
            Log.e("databaseViewPicHolder", "Tried to get missing picture or put it into a null holder.", e);
        }
    }

    public static class PicViewHolder extends RecyclerView.ViewHolder {
        ImageView personPic;

        PicViewHolder(View itemView) {
            super(itemView);
            personPic = itemView.findViewById(R.id.dataview_pic_container);
        }
    }
}
