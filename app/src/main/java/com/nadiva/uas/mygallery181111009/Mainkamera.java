package com.nadiva.uas.mygallery181111009;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.service.GpsTracker;
import com.nadiva.uas.mygallery181111009.viewmodel.MainKameraViewModel;
import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

public class Mainkamera extends AppCompatActivity {
    private static final int TAKE_PHOTO_CODE = 0;

    private MainKameraViewModel mainKameraViewModel;
    private ImageView imageViewGamabar;
    private Button buttonSimpan, buttonHapus, buttonAmbilGambar, buttonDapatkanLokasi;
    private TextView textViewLokasi;
    private String absolutePath;
    private Uri outputFileUri;

    private GpsTracker gpsTracker;

    private String address, fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainkamera);

        mainKameraViewModel = ViewModelProviders.of(this).get(MainKameraViewModel.class);
        imageViewGamabar = findViewById(R.id.imageViewGambar);
        buttonSimpan = findViewById(R.id.buttonSimpan);
        imageViewGamabar = findViewById(R.id.imageViewGambar);
        buttonHapus = findViewById(R.id.buttonHapus);
        buttonAmbilGambar = findViewById(R.id.buttonAmbilGambar);
        buttonDapatkanLokasi = findViewById(R.id.buttonDapatkanLokasi);
        textViewLokasi = findViewById(R.id.textViewLokasi);

        getNewLocation();

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+ getResources().getString(R.string.app_name) +"/";
        final File newDir = new File(dir);
        newDir.mkdirs();

        if (address != null) {
            textViewLokasi.setText(address);
        }

        buttonHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Pesan")
                        .setMessage("Apakah anda yakin ingin menghapus gambar ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageViewGamabar.setImageResource(0);
                                Toast.makeText(Mainkamera.this, "Gambar Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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

        buttonSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Pesan")
                        .setMessage("Apakah anda yakin ingin menyimpan gambar ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageModel imageModel = new ImageModel();
                                imageModel.setAbsolutePath(absolutePath);
                                imageModel.setAlamat(address);
                                mainKameraViewModel.insertImage(imageModel);
                                absolutePath = null;
                                Toast.makeText(Mainkamera.this, "Gambar Berhasil Disimpan!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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

        buttonAmbilGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address != null) {
                    fileName = dir + System.currentTimeMillis() + ".jpg";
                    File newFile = new File(fileName);
                    try {
                        //yang bagian ini buat ngecreate file nya aja
                        newFile.createNewFile();
                        absolutePath = newFile.getAbsolutePath();
                    } catch (IOException e) {
                        Log.e("ErrorFIle", e.getMessage());
                    }
                    //yang bagian ini codingan default sih wkwkwk tapi maskudnya itu file yang kita bikin tadi mau diambil Uri nya buat bisa diproses di kamera lewat provider
                    outputFileUri = FileProvider.getUriForFile(v.getContext(), v.getContext().getApplicationContext().getPackageName() + ".provider", newFile);

                    //yang bagian ini intent biasa buat ngebuka kamera nya
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //yang ini bagian nyimpen data Uri nya ke intent buat diproses jadi waktu nanti setelah capture image itu gambarnya disimpen ke variabel outputFileUri
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                    //yang ini buat ngejalanin intent nya
                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                } else {
                    Toast.makeText(v.getContext(), "Data lokasi tidak ditemukan, coba lagi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDapatkanLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = null;
                textViewLokasi.setText("");
                getNewLocation();
                Toast.makeText(v.getContext(), "Sedang mengambil data lokasi...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d("Success", "Picture Saved");
            if (absolutePath != null) {
                saveNewImage();
            }
        }
    }

    public void saveNewImage() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(absolutePath);
        Uri contentUri = Uri.fromFile(f);
        try {
            ExifInterface exifInterface = new ExifInterface(absolutePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
            }
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            bitmap = rotateBitmap(bitmap, orientation);
            OutputStream fileOutputStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            imageViewGamabar.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap rotateBitmap(Bitmap src, int orientation) {
        int w = src.getWidth();
        int h = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        Log.e("orientation", String.valueOf(orientation));
        Bitmap result = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);

        WatermarkText watermarkText = new WatermarkText(address)
                .setPositionX(0.01)
                .setPositionY(0.01)
                .setTextColor(Color.WHITE)
                .setTextAlpha(1000)
                .setTextSize(12);
        Bitmap newBitmap = WatermarkBuilder
                .create(this, result)
                .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                .getWatermark()
                .getOutputImage();

        return newBitmap;
    }

    public void getNewLocation(){
        gpsTracker = new GpsTracker(Mainkamera.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            try {
                Geocoder geocoder = new Geocoder(Mainkamera.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Log.e("address", addresses.get(0).getAddressLine(0));
                address = addresses.get(0).getAddressLine(0);
                textViewLokasi.setText(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            gpsTracker.showSettingsAlert();
        }
    }
}
