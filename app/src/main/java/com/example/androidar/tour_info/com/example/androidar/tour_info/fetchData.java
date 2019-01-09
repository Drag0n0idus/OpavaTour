package com.example.androidar.tour_info;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Otevře stream a stáhne JSON file (https://www.youtube.com/watch?v=Vcn4OuV4Ixg)
public class fetchData extends AsyncTask<String,Void,Void> {
    String data = "";
    String identifier = "";
    private TaskCompleted mCallback;
    ImageView imageView;
    Bitmap imgResult;


    public fetchData(TaskCompleted context){
        this.mCallback = context;
    }

    public fetchData(ImageView imageView){
        this.imageView = imageView;
    }

    // probíhá v pozadí aplikace,
    @Override
    protected Void doInBackground(String... params) {
        try {
            this.identifier=params[1]; //označení druhu dotazu
            URL url = new URL(params[0]); //url dotazu

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            if(this.identifier.equals("img")){
                imgResult=BitmapFactory.decodeStream(inputStream);
            }
            else {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    this.data = this.data + line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // proběhne po ukončení výpočtů v pozadí (doInBackground)
    @Override
    protected void onPostExecute(Void eVoid) {
        super.onPostExecute(eVoid);
        if(this.identifier.equals("img")){
            imageView.setImageBitmap(imgResult);
        }
        else {
            mCallback.onTaskComplete(this.data, this.identifier);
        }
    }
}