package com.example.androidar.tour_info;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Otevře stream a stáhne JSON file, který následně parasuje (https://www.youtube.com/watch?v=Vcn4OuV4Ixg)
public class fetchData extends AsyncTask<Void,Void,Void> {
    String data = "";
    String identifier = "tour";
    private TaskCompleted mCallback;


    public fetchData(TourInfo context){
        this.mCallback = context;

    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://192.168.2.130/android-project-server/www/api/tour?id=1");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        data="{\"tour\":\"TEST-tour\",\"author\":\"Admin\"}";

        return null;
    }


    @Override
    protected void onPostExecute(Void eVoid) {
        super.onPostExecute(eVoid);
        mCallback.onTaskComplete(data,identifier);
    }
}