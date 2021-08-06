package com.naitech.medicalLocator;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.naitech.medicalLocator.POJOs.Hospital;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private GoogleMap mMap;
    boolean locationPermissionGranted = false;
    Location lastKnownLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng defaultLocation = new LatLng(26.7034,27.8077);
    FloatingActionButton showlist;
    ArrayList<String> hospitals = new ArrayList<>();
    private double longitude, latitud;
    String hospital = "hospital";
    Object tranferData[] = new Object[2];
    GetNearByHospitals getNearByHospitals = new GetNearByHospitals();
    private int proximityRaduis = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadHospitals();

        showlist = findViewById(R.id.listHospitals);
        showlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("size in maps",String.format("%s",hospitals.size()));
                Intent hospitalsListIntent = new Intent(MapsActivity.this, ListOfHospitals.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("hospitals",hospitals);
                hospitalsListIntent.putExtras(bundle);
                startActivity(hospitalsListIntent);
            }
        });
    }

    private void loadHospitals() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("hospitals");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hospitals.clear();
                snapshot.getChildren().forEach(snap ->{
                    Hospital hospital = snap.getValue(Hospital.class);
                    hospitals.add(hospital.getName());
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      /*String url = getUrL(26.7034,27.8077, hospital);
        tranferData[0] = mMap;
        tranferData[1] = url;

        getNearByHospitals.execute(tranferData);
        Toast.makeText(this,"Searching for near by Hospitals", Toast.LENGTH_SHORT).show();*/
    }

    private String getUrL(double lat, double lng, String nearByhospital){
        StringBuilder googleUrl =new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location="+lat+","+lng);
        googleUrl.append("&radius="+proximityRaduis);
        googleUrl.append("&type="+nearByhospital);
        googleUrl.append("&sensor=true");
        googleUrl.append("&key="+"AIzaSyBCxMwuCxy04VgBehF2QCdIJvhZShh2IwU");

        Log.d("google maps url","url=" + googleUrl);
        return googleUrl.toString();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();

    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            latitud = lastKnownLocation.getLatitude();
                            longitude = lastKnownLocation.getLongitude();
                            if (lastKnownLocation != null) {
                                LatLng currLoc = new LatLng(latitud, longitude);
                                mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(currLoc).title("Me"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));
                            }
                        } else {
                            Log.d(null, "Current location is null. Using defaults.");
                            Log.e(null, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, 15));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
