package com.example.moodtracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.DB.MoodWriter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
    GoogleMap gMap;
    private MoodWriter moodWriter;
    private String email;
    private ArrayList<MoodEvent> moodEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent =  getIntent();
        email = intent.getStringExtra("email");

        moodWriter = ViewModelProviders.of(this).get(MoodWriter.class);
        moodWriter.init(email);

        moodWriter.getMoodEvents().observe(this, new Observer(){
            @Override
            public void onChanged(Object o) {
                moodEvents.clear();
                moodEvents.addAll((ArrayList<MoodEvent>) o);
                for(int i=0; i < moodEvents.size(); i++){
                    MoodEvent moodEvent = moodEvents.get(i);
                    if (moodEvent.getLatitude() != 0 && moodEvent.getLongitude() != 0){
                        //switch statement
                        switch(moodEvents.get(i).getEmotion())
                        {
                            case "happy" :
                                gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude()))
                                        .title("0x1F620")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                break;

                            case "sad" :
                                gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude()))
                                        .title("0x1F620")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                break;

                            case "angry" :
                                gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude()))
                                        .title("0x1F620")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                break;

                            case "neutral" :
                                gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude()))
                                        .title("0x1F620")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                break;
                        }
                    }

                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.

        // TODO: zoomin the camera to canada, go to the currentlocation first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            gMap.setOnMyLocationButtonClickListener(this);
            gMap.setOnMyLocationClickListener(this);
        } else {
            // Show rationale and request permission.
        }

    }

    // My Location layer and the My Location button to show user
    // the current position on the map
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}



