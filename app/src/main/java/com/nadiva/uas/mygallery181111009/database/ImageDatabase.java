package com.nadiva.uas.mygallery181111009.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ImageModel.class}, version = 2, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {
    private static final String DB_NAME = "image_db";
    private static ImageDatabase instance;
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static synchronized ImageDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ImageDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ImageDAO imageDAO();
}
