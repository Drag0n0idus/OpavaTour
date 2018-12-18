package com.example.androidar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidar.tour_info.TourInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView TourText;
    private GoogleMap mMap;
    private TourInfo tourInfo;
    private DirectionsResult result;
    private DateTime now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        String tourID = getIntent().getExtras().getString("QRresult");
        String pointsFetch = getIntent().getExtras().getString("pointsFetch");
        String tourName = getIntent().getExtras().getString("tourName");
        tourInfo = new TourInfo(tourID, tourName, pointsFetch);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Funkce onMapReady vytvoří instanci třídy TourInfo, která obsahuje informace a metody
     * potřebné k vytvoření trasy, kterou poté vytvoří a vykreslí.
     *
     * @param googleMap - instance třídy GoogleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        now = new DateTime();
        try {
            if (tourInfo.getWaypoints() == null) {
                result = DirectionsApi.newRequest(tourInfo.getGeoContext())
                        .mode(TravelMode.WALKING).origin(tourInfo.getOrigin()).destination(tourInfo.getDestination()).departureTime(now).await();
            } else {
                result = DirectionsApi.newRequest(tourInfo.getGeoContext())
                        .mode(TravelMode.WALKING).origin(tourInfo.getOrigin()).destination(tourInfo.getDestination()).waypoints(tourInfo.getWaypoints()).departureTime(now).await();
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        mMap.setMyLocationEnabled(true);

        tourInfo.addMarkersToMap(result, mMap);
        tourInfo.addPolyline(result, mMap);
    }


    public void TourName(String name){
        this.TourText.setBackgroundColor(Color.BLUE);
        this.TourText.setText(name);
    }


    /*Spuštění navigace
    public void launchNavigation(View view){
        //Vytvoření url požadavku pro navigaci
        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + hlaska.latitude + "%2C" + hlaska.longitude + "&destination=" + obecniDum.latitude + "%2C" + obecniDum.longitude + "&waypoints=" + slezskeDivadlo.latitude + "%2C" + slezskeDivadlo.longitude + "%7C" + kostelMarie.latitude + "%2C" + kostelMarie.longitude + "&travelmode=walking&dir_action=navigate";
        //Vytvoření intentu pro volání navigace a následné volání navigace
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }*/
}
