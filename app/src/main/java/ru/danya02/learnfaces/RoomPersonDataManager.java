package ru.danya02.learnfaces;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Database(entities = {PersonListing.class, PersonEntity.class, Picture.class}, version = 1)
public abstract class RoomPersonDataManager extends RoomDatabase {
    static RoomPersonDataManager db = null;

    static RoomPersonDataManager getInstance(Context appContext){
        if(db != null){return db;}
        db =  Room.databaseBuilder(appContext, RoomPersonDataManager.class, "person_data").build();

        return db;
    }

    public abstract PersonListingDao personListingDao();
    public abstract PersonDao personDao();
}


@Entity(tableName = "person_listing")
class PersonListing {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "last_checked_at_unixtime")
    public long lastCheckedAtUnixtime;

    @ColumnInfo(name = "last_checked_version")
    public long lastCheckedVersion;

    @ColumnInfo(name = "http_basic_auth_username")
    public String httpBasicAuthUsername;

    @ColumnInfo(name = "http_basic_auth_password")
    public String httpBasicAuthPassword;
}

@Entity(tableName = "person")
class PersonEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;
}

@Entity(tableName = "picture")
class Picture {
    @PrimaryKey
    @NonNull
    public String sha256hash="[invalid]";

    @ColumnInfo(name = "personInPictureId")
    public long personInPictureId;

    @ColumnInfo(name = "pictureData", typeAffinity = ColumnInfo.BLOB)
    public byte[] pictureData;
}

class PersonWithPictures {
    @Embedded public PersonEntity person;
    @Relation(parentColumn = "id", entityColumn = "personInPictureId")
    public List<Picture> pictures;
}



@Dao
interface PersonListingDao {
    @Query("SELECT * FROM person_listing")
    List<PersonListing> getAll();

    @Insert
    void insert(PersonListing listing);

    @Update
    void update(PersonListing listing);

    @Delete
    void delete(PersonListing listing);
}

@Dao
interface PersonDao {
    @Transaction
    @Query("SELECT * FROM person")
    List<PersonWithPictures> getPeopleWithPictures();

    @Query("SELECT COUNT(*) FROM person")
    LiveData<Long> getCount();

}