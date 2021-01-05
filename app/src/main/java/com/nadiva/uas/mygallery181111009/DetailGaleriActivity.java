package com.nadiva.uas.mygallery181111009;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.viewmodel.DetailGalariViewModel;

import java.io.File;

public class DetailGaleriActivity extends AppCompatActivity {
    private DetailGalariViewModel detailGalariViewModel;
    private ImageView imageViewGamber;
    private TextView textViewNama, textViewLokasi;
    private Button buttonBagikan, buttonHapus;
    private ImageModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_galeri);

        detailGalariViewModel = ViewModelProviders.of(this).get(DetailGalariViewModel.class);
        imageModel = (ImageModel) getIntent().getSerializableExtra("IMAGE");
        final Uri uri = Uri.fromFile(new File(imageModel.getAbsolutePath()));

        imageViewGamber = findViewById(R.id.imageViewGambar);
        textViewNama = findViewById(R.id.textViewNama);
        textViewLokasi = findViewById(R.id.textViewLokasi);
        buttonBagikan = findViewById(R.id.buttonBagikan);
        buttonHapus = findViewById(R.id.buttonHapus);

        imageViewGamber.setImageURI(uri);
        textViewNama.setText(imageModel.getNama());
        textViewLokasi.setText(imageModel.getAlamat());

        buttonBagikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imageModel.getAbsolutePath());
                Uri imageUri = FileProvider.getUriForFile(v.getContext(), "com.nadiva.uas.mygallery181111009.provider", file);
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("*/*");
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                myIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                v.getContext().startActivity(Intent.createChooser(myIntent, "Bagikan Gambar Ini"));
            }
        });

        buttonHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Pesan")
                        .setMessage("Apakah anda ingin menghapus gambar ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File fileDelete = new File(uri.getPath());
                                if (fileDelete.exists()) {
                                    if (fileDelete.delete()) {
                                        System.out.println("file Deleted :" + uri.getPath());
                                    } else {
                                        System.out.println("file not Deleted :" + uri.getPath());
                                    }
                                }
                                detailGalariViewModel.deleteImage(imageModel);
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
}