package com.nadiva.uas.mygallery181111009.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDAO {
    @Query("SELECT * FROM ImageModel")
    LiveData<List<ImageModel>> getImages();

    @Insert
    void insertImage(ImageModel imageModel);

    @Delete
    void deleteImage(ImageModel imageModel);
}
