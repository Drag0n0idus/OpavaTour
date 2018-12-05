package com.example.androidar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.androidar.tour_info.TaskCompleted;
import com.example.androidar.tour_info.fetchData;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
// https://www.youtube.com/watch?v=otkz5Cwdw38&t=924s
public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, TaskCompleted {

    private final String apiServer = "http://www.garttox.jedovarnik.cz/api/";
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private String myResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QRActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        myResult = result.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(myResult.contains("|")){
            final String splited[] = myResult.split(Pattern.quote("|"));
            if(splited[0].equals("OpavaTour") && splited[1].matches("[0-9]+")){
                new fetchData(QRActivity.this).execute("","");

            }
            else{
                builder.setPositiveButton("Zpět", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.resumeCameraPreview(QRActivity.this);
                    }
                });
                builder.setMessage("QR kód má špatný formát");
            }
        }
        else {
            builder.setPositiveButton("Zpět", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(QRActivity.this);
                }
            });
            builder.setMessage("QR kód neobsahuje stezku");
        }
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public void verified(String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Zpět", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(QRActivity.this);
            }
        });
        builder.setNeutralButton("Ano", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openMaps(myResult);
            }
        });
        builder.setMessage("Přejete si spustit stezku: "+ name);
    }

    //Spuštění Map
    public void openMaps(String result) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("QRresult",result);
        startActivity(intent);
    }

    @Override
    public void onTaskComplete(String result, String identifier){
        JSONObject JObject= null;
        try {
            JObject = new JSONObject(result);
            String status=(String)JObject.get("status");
            if(status.equals("avaible")){
                this.verified((String)JObject.get("title"));
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setPositiveButton("Zpět", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.resumeCameraPreview(QRActivity.this);
                    }
                });
                builder.setMessage("Tahle stezka buď již nefunguje nebo neexistuje");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
