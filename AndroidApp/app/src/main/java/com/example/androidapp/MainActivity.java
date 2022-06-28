package com.example.androidapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    protected LocationManager locationManager;
    protected String latitude, longitude;
    protected Date date;
    protected boolean gps_enabled, network_enabled;
    protected Timestamp ts;
    protected long time;
    Handler handler;
    SupportMapFragment mapFragment;
    ServerInteraction thread;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.floatingActionButton);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
        thread = new ServerInteraction("104.248.165.64", "5536", handler, MainActivity.this);
        thread.start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = new Date();
                time = date.getTime();
                ts = new Timestamp(time);
                JSONObject jsonObject = createJsonObject(longitude, latitude);
                openDialog();
                System.out.println(jsonObject);
                thread.sendJSONToServer(jsonObject);
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        UiSettings map_ui = googleMap.getUiSettings();
//        googleMap.setMinZoomPreference(50);
        map_ui.setMyLocationButtonEnabled(true);
//        map_ui.setCompassEnabled(true);
        map_ui.setZoomControlsEnabled(true);
        LatLng loc = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            googleMap.setMyLocationEnabled(true);
            return;
        }
        //googleMap.addMarker(new MarkerOptions().position(loc).title("MyLocation"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15));
    }


    public boolean checkPermissions(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            requestGPS();
            return false;
        }
    }

    public void openDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Reported Information")
                .setMessage("Date/ Time: " + ts + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();
        setLongitudeAndLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setLongitudeAndLatitude(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
            latitude = String.valueOf(lastKnownLocation().getLatitude());
            longitude = String.valueOf(lastKnownLocation().getLongitude());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLongitudeAndLatitude();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    public Location lastKnownLocation(){
        Location bestLocation = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String provider : locationManager.getProviders(true)) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation == null) {
                    continue;
                }
                if (bestLocation == null || lastKnownLocation.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = lastKnownLocation;
                }
            }
        }
        return bestLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLongitudeAndLatitude();
                }
            }
    }

    public void requestGPS (){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission to access Location needed")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
        }
    }

    public JSONObject createJsonObject(String longitude, String latitude){
        String reason = "Quick Report";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);
            jsonObject.put("reason", reason);
        }
        catch ( JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
