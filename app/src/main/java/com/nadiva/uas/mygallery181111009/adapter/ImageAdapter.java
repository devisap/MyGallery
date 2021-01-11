package com.nadiva.uas.mygallery181111009.adapter;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.nadiva.uas.mygallery181111009.DetailGaleriActivity;
import com.nadiva.uas.mygallery181111009.R;
import com.nadiva.uas.mygallery181111009.database.ImageModel;
import com.nadiva.uas.mygallery181111009.repository.ImageRepository;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<ImageModel> list;
    private ImageRepository imageRepository;

    public ImageAdapter(List<ImageModel> list, Application application) {
        this.list = list;
        imageRepository = new ImageRepository(application);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_galeri, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        byte[] decodedString = Base64.decode(list.get(position).getImage(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageView.setImageBitmap(decodedImage);
//        Uri uri = Uri.fromFile(new File(list.get(position).getAbsolutePath()));
//        holder.imageView.setImageURI(uri);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailGaleriActivity.class);
                intent.putExtra("ID_IMAGE", list.get(position).getIdImage());
                v.getContext().startActivity(intent);
            }
        });
//        holder.textViewAlamat.setText(list.get(position).getAlamat());
//        holder.buttonHapus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(v.getContext())
//                        .setTitle("Pesan")
//                        .setMessage("Apakah anda ingin menghapus gambar ini?")
//                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                File fileDelete = new File(uri.getPath());
//                                if (fileDelete.exists()) {
//                                    if (fileDelete.delete()) {
//                                        System.out.println("file Deleted :" + uri.getPath());
//                                    } else {
//                                        System.out.println("file not Deleted :" + uri.getPath());
//                                    }
//                                }
//                                imageRepository.deleteImage(list.get(position));
//                                dialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create()
//                        .show();
//            }
//        });
//
//        holder.buttonBagikan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File file = new File(list.get(position).getAbsolutePath());
//                Uri imageUri = FileProvider.getUriForFile(v.getContext(), "com.android.locationcamera.provider", file);
//                Intent myIntent = new Intent(Intent.ACTION_SEND);
//                myIntent.setType("*/*");
//                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                myIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                v.getContext().startActivity(Intent.createChooser(myIntent, "Bagikan Gambar Ini"));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
