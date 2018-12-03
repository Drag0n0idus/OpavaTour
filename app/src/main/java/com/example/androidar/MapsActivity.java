package com.example.androidar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
        String tourID= getIntent().getExtras().getString("QRresult");
        tourInfo = new TourInfo(MapsActivity.this, tourID);
        TourText = (TextView) findViewById(R.id.textView4);
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
    }

    public void startMap(){

        now = new DateTime();
        try {
            if(tourInfo.getWaypoints() == null){
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
