package com.nadiva.uas.mygallery181111009.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.repository.ImageRepository;

import java.util.List;

public class DetailGaleriViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;

    public DetailGaleriViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public void deleteImage(ImageModel imageModel) {
        imageRepository.deleteImage(imageModel);
    }

    public LiveData<List<ImageModel>> getImagesDetail(int idImage) {
        return imageRepository.getImagesDetail(idImage);
    }
}
