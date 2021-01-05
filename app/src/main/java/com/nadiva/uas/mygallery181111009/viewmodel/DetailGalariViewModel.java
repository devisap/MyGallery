package com.nadiva.uas.mygallery181111009.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.repository.ImageRepository;

public class DetailGalariViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;

    public DetailGalariViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public void deleteImage(ImageModel imageModel) {
        imageRepository.deleteImage(imageModel);
    }
}
