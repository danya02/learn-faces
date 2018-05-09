package ru.danya02.learnfaces;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.PersonViewHolder> {


    ArrayList<Person> persons;


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
        PicsAdapter adapter = new PicsAdapter(pics);
        pics.add(persons.get(position).main_pic);
        pics.addAll(persons.get(position).alt_pics);
        holder.personPics.setAdapter(adapter);
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

    public PicsAdapter(ArrayList<String> pics) {
        this.pics = pics;
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }

    @NonNull
    @Override
    public PicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.database_view_item, viewGroup, false);
        return new PicsAdapter.PicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, int position) {
        Glide.with(holder.itemView).load(pics.get(position)).into(holder.personPic);
    }

    public static class PicViewHolder extends RecyclerView.ViewHolder {
        ImageView personPic;

        PicViewHolder(View itemView) {
            super(itemView);
            personPic = itemView.findViewById(R.id.pic_container);
        }
    }
}
