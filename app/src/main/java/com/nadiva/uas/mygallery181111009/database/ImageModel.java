package com.nadiva.uas.mygallery181111009.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ImageModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idImage")
    private int idImage;

    @ColumnInfo(name = "absolutePath")
    private String absolutePath;

    @ColumnInfo(name = "alamat")
    private String alamat;

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

}
