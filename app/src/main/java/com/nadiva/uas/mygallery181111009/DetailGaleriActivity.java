package com.nadiva.uas.mygallery181111009;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.viewmodel.DetailGaleriViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DetailGaleriActivity extends AppCompatActivity {
    private DetailGaleriViewModel detailGaleriViewModel;
    private ImageView imageViewGamber;
    private TextView textViewNama, textViewLokasi;
    private Button buttonBagikan, buttonHapus;
    private int idImage;
    private String alamat, image;
    private ImageModel model;
    private Bitmap decodedImage;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_galeri);

        detailGaleriViewModel = ViewModelProviders.of(this).get(DetailGaleriViewModel.class);
        idImage = getIntent().getIntExtra("ID_IMAGE", 0);
        imageViewGamber = findViewById(R.id.imageViewGambar);
        textViewLokasi = findViewById(R.id.textViewLokasi);
        buttonBagikan = findViewById(R.id.buttonBagikan);
        buttonHapus = findViewById(R.id.buttonHapus);

        detailGaleriViewModel.getImagesDetail(idImage).observe(this, new Observer<List<ImageModel>>() {
            @Override
            public void onChanged(List<ImageModel> imageModels) {
                for (ImageModel imageModel: imageModels) {
                    model = new ImageModel();
                    model.setIdImage(idImage);
                    model.setImage(imageModel.getImage());
                    model.setAlamat(imageModel.getAlamat());

                    byte[] decodedString = Base64.decode(imageModel.getImage(), Base64.DEFAULT);
                    decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageViewGamber.setImageBitmap(decodedImage);
                    textViewLokasi.setText(imageModel.getAlamat());
                }
            }
        });

        buttonBagikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file = createTempFile(decodedImage);
                Uri imageUri = FileProvider.getUriForFile(v.getContext(), "com.nadiva.uas.mygallery181111009.provider", file);
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("*/*");
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                myIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivityForResult(Intent.createChooser(myIntent, "Bagikan Gambar Ini"), 0);
//                v.getContext().startActivity(Intent.createChooser(myIntent, "Bagikan Gambar Ini"));
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
                                detailGaleriViewModel.deleteImage(model);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            File fileDelete = file;
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    System.out.println("file Deleted :");
                } else {
                    System.out.println("file not Deleted :");
                }
            }
        }
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + ".PNG");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        //write the bytes in file
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArray);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}