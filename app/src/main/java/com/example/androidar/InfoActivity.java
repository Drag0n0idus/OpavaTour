package com.example.androidar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidar.tour_info.fetchData;

public class InfoActivity extends AppCompatActivity {

    private TextView title,text;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView title = (TextView) findViewById(R.id.title);
        TextView text = (TextView) findViewById(R.id.text);

        ImageView image = (ImageView) findViewById(R.id.image);

        title.setText(getIntent().getExtras().getString("Title"));
        text.setText(getIntent().getExtras().getString("Text"));

        new fetchData(image).execute(getIntent().getExtras().getString("Img"),"img");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
