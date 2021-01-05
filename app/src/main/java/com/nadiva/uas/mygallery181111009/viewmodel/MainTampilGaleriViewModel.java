package com.nadiva.uas.mygallery181111009.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.repository.ImageRepository;

import java.util.List;

public class MainTampilGaleriViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;

    public MainTampilGaleriViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public LiveData<List<ImageModel>> getImages() {
        return imageRepository.getImages();
    }
}
