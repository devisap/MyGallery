package com.nadiva.uas.mygallery181111009;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnkamera=(ImageButton)findViewById(R.id.kamera);
        btnkamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kamera = new Intent(MainActivity.this, Mainkamera.class);
                startActivity(kamera);
            }
        });

        ImageButton btngaleri=(ImageButton)findViewById(R.id.galeri);
        btngaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeri = new Intent(MainActivity.this, Maintampilgaleri.class);
                startActivity(galeri);
            }
        });

        ImageButton btnhelp=(ImageButton)findViewById(R.id.help);
        btnhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(MainActivity.this, Mainhelp.class);
                startActivity(help);
            }
        });

        ImageButton btnabout=(ImageButton)findViewById(R.id.about);
        btnabout .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent about = new Intent(MainActivity.this, Mainabout.class);
                startActivity(about);
            }
        });
    }
}