package com.example.androidar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidar.tour_info.Point;
import com.example.androidar.tour_info.TourInfo;
import com.example.androidar.tour_info.fetchData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static android.location.Location.distanceBetween;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private String TourName;
    private TextView TourText;
    private TextView TourLat;
    private TextView TourLong;
    private TextView TourPoint;
    private GoogleMap mMap;
    private TourInfo tourInfo;
    private DirectionsResult result;
    private DateTime now;
    private Button btn;
    private Location lastLocation;
    private Boolean[] visited;
    private int pointVisited;
    private GoogleApiClient googleApiClient;
    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btn = (Button) findViewById(R.id.infoButton);
        TourText = (TextView) findViewById(R.id.textView4);
        TourLat = (TextView) findViewById(R.id.textLat);
        TourLong = (TextView) findViewById(R.id.textLong);
        // create GoogleApiClient
        createGoogleApi();
        String tourID = getIntent().getExtras().getString("QRresult");
        String pointsFetch = getIntent().getExtras().getString("pointsFetch");
        TourName = getIntent().getExtras().getString("tourName");
        tourInfo = new TourInfo(tourID, TourName, pointsFetch,MapsActivity.this);
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
        visited = new Boolean[tourInfo.getMarkers().length];
        for(int i = 0; i < visited.length; i++){
            visited[i] = false;
        }
        tourInfo.addPolyline(result, mMap);
    }


    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    // Get last known location
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if ( lastLocation != null ) {
            Log.i(TAG, "LasKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            writeLastLocation();
            startLocationUpdates();
        } else {
            Log.w(TAG, "No location retrieved yet");
            startLocationUpdates();
        }
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  100;
    private final int FASTEST_INTERVAL = 90;

    // Start location Updates
    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
        int id=isInDistance(location);
        if(id>=0) {
            this.tourInfo.readDetail(id);
        }
        else {
            btn.setVisibility(View.INVISIBLE);
        }
    }

    private int isInDistance(Location location) {
        Location endLoc = new Location("");
        Location startLoc = new Location("");
        startLoc.setLatitude(location.getLatitude());
        startLoc.setLongitude(location.getLongitude());
        for(int i = 0; i < tourInfo.getPoints().length; i++) {
            endLoc.setLatitude((double) Double.parseDouble(tourInfo.getPoints()[i].getLatitude()));
            endLoc.setLongitude((double) Double.parseDouble(tourInfo.getPoints()[i].getLongitude()));
            if(Math.ceil(startLoc.distanceTo(endLoc)) <= 50) {
                btn.setText("Zajímavosti o " + tourInfo.getPoints()[i].getName());
                pointVisited = i;
                return tourInfo.getPoints()[i].getId();
            }
        }

        return -1;
    }

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        TourLat.setText( "Lat: " + location.getLatitude() );
        TourLong.setText( "Long: " + location.getLongitude() );
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
    }

    public void TourName(String name){
        this.TourText.setBackgroundColor(Color.BLUE);
        this.TourText.setText(name);
    }

    public void openInfo(View view) {
        markerVisited();
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("Text", this.tourInfo.currentPointText);
        intent.putExtra("Title", this.tourInfo.currentPointTitle);
        intent.putExtra("Img", this.tourInfo.currentPointImg);
        startActivity(intent);
    }

    public void markerVisited() {
        visited[pointVisited] = true;
        tourInfo.getMarkers()[pointVisited].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if(finishedTour()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setNegativeButton("Ukončit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finished();
                }
            });
            builder.setMessage("Dokončili jste stezku " + TourName + "!");
            AlertDialog alert1 = builder.create();
            alert1.show();
        }
    }

    public boolean finishedTour() {
        for(int i = 0; i < visited.length; i++){
            if(visited[i] == false) {
                return false;
            }
        }
        return true;
    }

    public void finished() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void detailReady(){
        btn.setVisibility(View.VISIBLE);
    }
}