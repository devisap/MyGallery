package com.nadiva.uas.mygallery181111009;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.viewmodel.MainKameraViewModel;

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
    private Button buttonSimpan, buttonHapus, buttonAmbilGambar;
    private EditText editTextNamaGambar;
    private TextView textViewLokasi;
    private String absolutePath;
    private Uri outputFileUri;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private String address, fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainkamera);

        mainKameraViewModel = ViewModelProviders.of(this).get(MainKameraViewModel.class);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        imageViewGamabar = findViewById(R.id.imageViewGambar);
        buttonSimpan = findViewById(R.id.buttonSimpan);
        imageViewGamabar = findViewById(R.id.imageViewGambar);
        buttonHapus = findViewById(R.id.buttonHapus);
        buttonAmbilGambar = findViewById(R.id.buttonAmbilGambar);
        editTextNamaGambar = findViewById(R.id.editTextNamaGambar);
        textViewLokasi = findViewById(R.id.textViewLokasi);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
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
                                imageModel.setNama(editTextNamaGambar.getText().toString());
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
                if (address == null) {
                    getLocation();
                    Toast.makeText(Mainkamera.this, "Coba lagi, aplikasi gagal mendapatkan lokasi!", Toast.LENGTH_SHORT).show();
                } else {
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
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d("Success", "Picture Saved");
            if (absolutePath != null) {
                addWatermarkToImage();
            }
        }
    }

    public static Bitmap addWatermark(Bitmap src, String watermark, int orientation) {
        int w = src.getWidth();
        int h = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        Log.e("orientation", String.valueOf(orientation));
        Bitmap result = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);
        Canvas canvas = new Canvas(result);
        //canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(src, matrix, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(100);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(watermark, 0, (0+paint.getTextSize()), paint);

        return result;
    }

    public void addWatermarkToImage() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(absolutePath);
        Uri contentUri = Uri.fromFile(f);
        Bitmap bitmap;
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
            }
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            bitmap = addWatermark(bitmap, address, orientation);
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

    private void OnGPS() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                getLocation();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(Mainkamera.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Log.e("address", addresses.get(0).getAddressLine(0));
                            address = addresses.get(0).getAddressLine(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
//                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                try {
                                    Geocoder geocoder = new Geocoder(Mainkamera.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location1.getLatitude(), location.getLongitude(), 1);
                                    Log.e("address", addresses.get(0).getAddressLine(0));
                                    address = addresses.get(0).getAddressLine(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        }
    }
}
