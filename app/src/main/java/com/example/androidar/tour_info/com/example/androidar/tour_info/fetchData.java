package com.example.androidar.tour_info;

import android.content.Context;
import android.os.AsyncTask;

import com.example.androidar.HowActivity;

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
    String dataParsed = "";
    String singleParsed = "";
    private TourInfo mContext;
    private TaskCompleted mCallback;


    public fetchData(TourInfo context){
        this.mContext = context;
        this.mCallback = (TaskCompleted) context;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        /*try {
            URL url = new URL("http://192.168.2.130/android-project-server/www/api/tour?id=1");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }*/
            data="TEST-NAME";


            /*JSONArray JA = new JSONArray(data);
            for(int i = 0;i <JA.length();i++){
                JSONObject JO = (JSONObject) JA.get(i);
                singleParsed =  "Name:" + JO.get("name") + "\n"+
                        "Password:" + JO.get("password") + "\n"+
                        "Contact:" + JO.get("contact") + "\n"+
                        "Country:" + JO.get("country") + "\n";

                dataParsed = dataParsed + singleParsed + "\n";
            }*/





        /*} catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/ /*catch (JSONException e) {
            e.printStackTrace();
        }*/


        return null;
    }



    @Override
    protected void onPostExecute(Void eVoid) {
        super.onPostExecute(eVoid);
        mCallback.onTaskComplete(data);
    }
}