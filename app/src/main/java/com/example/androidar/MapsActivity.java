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
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String hlaska = "49.938887, 17.902368";
    String slezskeDivadlo = "49.938963, 17.901628";
    String kostelMarie = "49.938773, 17.900169";
    String obecniDum = "49.936215, 17.901329";
    DirectionsResult result;
    DateTime now;

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

        now = new DateTime();
        try {
            result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.WALKING).origin(hlaska).destination(obecniDum)
                    .waypoints(slezskeDivadlo, kostelMarie).departureTime(now).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addMarkersToMap(result, mMap);
        addPolyline(result, mMap);

        /*Přidá markery na daných koordinátech
        mMap.addMarker(new MarkerOptions().position(hlaska).title("Hláska"));
        mMap.addMarker(new MarkerOptions().position(slezskeDivadlo).title("Slezské Divadlo"));
        mMap.addMarker(new MarkerOptions().position(kostelMarie).title("Konkatedrála Nanebevzetí Panny Marie"));
        mMap.addMarker(new MarkerOptions().position(obecniDum).title("Obecní Dům"));



        //Vytvoří spojnici mezi markery
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        hlaska,
                        slezskeDivadlo,
                        sDtokM1,
                        sDtokM2,
                        kostelMarie,
                        kMtooD1,
                        kMtooD2,
                        kMtooD3,
                        kMtooD4,
                        obecniDum));*/
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey("AIzaSyCF241i93KMq6y-tHtrQoVhGtGweauHSk4")
                .setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        //Leg 0 Start -> Hláska | Leg 0 End -> Divadlo
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,
                results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,
                results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].endAddress));
        //Leg 1 Start -> Divadlo | Leg 1 End -> Kostel
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[1].endLocation.lat,
                results.routes[0].legs[1].endLocation.lng)).title(results.routes[0].legs[1].endAddress));
        //Leg 2 Start -> Kostel | Leg 2 End -> Obecní dům
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[2].endLocation.lat,
                results.routes[0].legs[2].endLocation.lng)).title(results.routes[0].legs[2].endAddress));
        //Posune kameru na určený bod
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(results.routes[0].legs[0].startLocation.lat,
                results.routes[0].legs[0].startLocation.lng)));
        //Nastavení min a max přiblížení
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(18.0f);
    }

    private String getEndLocationTitle(DirectionsResult results, int leg){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
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
