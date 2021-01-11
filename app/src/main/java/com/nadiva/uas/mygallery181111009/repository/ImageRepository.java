package com.nadiva.uas.mygallery181111009.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nadiva.uas.mygallery181111009.database.ImageDAO;
import com.nadiva.uas.mygallery181111009.database.ImageDatabase;
import com.nadiva.uas.mygallery181111009.database.ImageModel;

import java.util.List;

public class ImageRepository {
    private ImageDAO imageDAO;

    public ImageRepository(Application application) {
        this.imageDAO = ImageDatabase.getInstance(application).imageDAO();
    }

    public LiveData<List<ImageModel>> getImages() {
        return imageDAO.getImages();
    }

    public LiveData<List<ImageModel>> getImagesDetail(int idImage) {
        return imageDAO.getImagesDetail(idImage);
    }

    public void insertImage(final ImageModel imageModel) {
        ImageDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                imageDAO.insertImage(imageModel);
            }
        });
    }

    public void deleteImage(final ImageModel imageModel) {
        ImageDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                imageDAO.deleteImage(imageModel);
            }
        });
    }
}
