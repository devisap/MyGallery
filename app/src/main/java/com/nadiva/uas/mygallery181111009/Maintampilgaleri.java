package com.nadiva.uas.mygallery181111009;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.nadiva.uas.mygallery181111009.adapter.ImageAdapter;
import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.viewmodel.MainTampilGaleriViewModel;

import java.util.List;

public class Maintampilgaleri extends AppCompatActivity {
    private MainTampilGaleriViewModel mainTampilGaleriViewModel;
    private RecyclerView recyclerViewGaleri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintampilgaleri);

        mainTampilGaleriViewModel = ViewModelProviders.of(this).get(MainTampilGaleriViewModel.class);
        recyclerViewGaleri = findViewById(R.id.recyclerViewGaleri);

        mainTampilGaleriViewModel.getImages().observe(this, new Observer<List<ImageModel>>() {
            @Override
            public void onChanged(List<ImageModel> imageModels) {
                for (ImageModel imageModel: imageModels) {
                    Log.e("idImage", String.valueOf(imageModel.getIdImage()));
                    Log.e("absolutePath", String.valueOf(imageModel.getAbsolutePath()));
                }
                recyclerViewGaleri(imageModels);
            }
        });
    }

    public void recyclerViewGaleri(List<ImageModel> list) {
        ImageAdapter imageAdapter = new ImageAdapter(list, getApplication());
        recyclerViewGaleri.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerViewGaleri.setAdapter(imageAdapter);
    }
}