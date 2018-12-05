package com.example.androidar;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = (ImageView) findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.logored);
    }

    //Spuštění Map
    public void openMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //Spuštění QR
    public void openQR(View view) {
        Intent intent = new Intent(this, QRActivity.class);
        startActivity(intent);
    }

    //Spuštění How To
    public void openHow(View view) {
        Intent intent = new Intent(this, HowActivity.class);
        startActivity(intent);
    }
}
