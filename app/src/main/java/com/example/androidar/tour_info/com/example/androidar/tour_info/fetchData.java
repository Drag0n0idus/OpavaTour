package com.example.androidar.tour_info;

import android.os.AsyncTask;

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


    public fetchData(TaskCompleted context){
        this.mCallback = context;

    }

    // probíhá v pozadí aplikace,
    @Override
    protected Void doInBackground(String... params) {
        try {
            this.identifier=params[1]; //označení druhu dotazu
            URL url = new URL(params[0]); //url dotazu
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                this.data = this.data + line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // proběhne po ukončení výpočtů v pozadí (doInBackground)
    @Override
    protected void onPostExecute(Void eVoid) {
        super.onPostExecute(eVoid);
        mCallback.onTaskComplete(this.data,this.identifier);
    }
}