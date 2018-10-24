package com.example.androidar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng hlaska = new LatLng(49.938849, 17.902541);;
    LatLng slezskeDivadlo = new LatLng(49.939067, 17.90118899999993);;
    LatLng kostelMarie = new LatLng(49.9386939, 17.900636599999984);;
    LatLng obecniDum = new LatLng(49.93627182024, 17.90123462677002);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Vytvoření mapFragment - objekt držící mapu
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Funkce na vytvoření mapy a přidání markerů
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Přidá markery na daných koordinátech
        mMap.addMarker(new MarkerOptions().position(hlaska).title("Hláska"));
        mMap.addMarker(new MarkerOptions().position(slezskeDivadlo).title("Slezské Divadlo"));
        mMap.addMarker(new MarkerOptions().position(kostelMarie).title("Konkatedrála Nanebevzetí Panny Marie"));
        mMap.addMarker(new MarkerOptions().position(obecniDum).title("Obecní Dům"));
        //Posune kameru na určený bod
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hlaska));
        //Nastavení min a max přiblížení
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(18.0f);

        //Vytvoří spojnici mezi markery
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        hlaska,
                        slezskeDivadlo,
                        kostelMarie,
                        obecniDum));
    }

    //Spuštění navigace
    public void launchNavigation(View view){
        //Vytvoření url požadavku pro navigaci
        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + hlaska.latitude + "%2C" + hlaska.longitude + "&destination=" + obecniDum.latitude + "%2C" + obecniDum.longitude + "&waypoints=" + slezskeDivadlo.latitude + "%2C" + slezskeDivadlo.longitude + "%7C" + kostelMarie.latitude + "%2C" + kostelMarie.longitude + "&travelmode=walking&dir_action=navigate";
        //Vytvoření intentu pro volání navigace a následné volání navigace
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}
