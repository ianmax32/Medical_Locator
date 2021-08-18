package com.naitech.medicalLocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
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
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private GoogleMap mMap;
    boolean locationPermissionGranted = false;
    private Location lastKnownLocation;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng defaultLocation = new LatLng(26.7034,27.8077);
    private FloatingActionButton showlist;
    private ArrayList<String> hospitals = new ArrayList<>();
    private double longitude, latitud;
    private String hospital = "hospital";
    private Object tranferData[] = new Object[2];
    private GetNearByHospitals getNearByHospitals = new GetNearByHospitals();
    private int proximityRaduis = 10000;
    private String url = "";
    HashMap<String, String> googleNearByHospitalspec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleNearByHospitalspec = new HashMap<>();

        Toast.makeText(this,"Searching for near by Hospitals", Toast.LENGTH_LONG).show();

        showlist = findViewById(R.id.listHospitals);
        showlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent hospitalsListIntent = new Intent(MapsActivity.this, ListOfHospitals.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("hospitals", googleNearByHospitalspec);
                hospitalsListIntent.putExtras(bundle);
                startActivity(hospitalsListIntent);
            }
        });
    }


    private String getUrL(double lat, double lng, String nearByhospital){
        StringBuilder googleUrl =new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googleUrl.append("location="+lat+","+lng);
        googleUrl.append("&radius="+proximityRaduis);
        googleUrl.append("&query="+nearByhospital);
        googleUrl.append("&query="+"medical clinics");
        googleUrl.append("&sensor=true");
        googleUrl.append("&fields=name,rating,formatted_phone_number");
        googleUrl.append("&key="+"AIzaSyBP4T78uTEEN0uD1lIwV7OGwygW8Bxbq7E");

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

    private void getDeviceLocation(){
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

                            Log.d("location", String.valueOf(latitud) + " "+ String.valueOf(longitude));
                            if (lastKnownLocation != null) {
                                LatLng currLoc = new LatLng(latitud, longitude);
                                mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(currLoc).title("Me"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));

                                url = getUrL(latitud,longitude, hospital);
                                Log.d("location cord",String.valueOf(latitud) + " "+ String.valueOf(longitude));
                                tranferData[0] = mMap;
                                tranferData[1] = url;
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        new AlertDialog.Builder(MapsActivity.this)
                                                .setTitle(marker.getTitle())
                                                .setIcon(R.drawable.medicalloc)
                                                .setMessage(marker.getSnippet())
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                        return false;
                                    }
                                });
                                getNearByHospitals.execute(tranferData);
                                updateLocationUI();
                                //googleNearByHospitalspec = getNearByHospitals.getHospitals();
                                //Log.d("size in maps",String.format("%s",googleNearByHospitalspec.size()));
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
