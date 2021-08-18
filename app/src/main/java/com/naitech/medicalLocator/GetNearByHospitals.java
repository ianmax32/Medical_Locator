package com.naitech.medicalLocator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearByHospitals extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;
    private GoogleMap nMap;
    HashMap<String, String> googleNearByHospital;

    @Override
    protected String doInBackground(Object... objects) {
        nMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlaceData = downloadURL.readUrl(url);
            Log.d("google places url",googlePlaceData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearByHospitalsList = null;
        DataParser dataParser = new DataParser();
        nearByHospitalsList = dataParser.parse(s);


        DisplayNearByHospitals(nearByHospitalsList);
    }

    private void DisplayNearByHospitals(List<HashMap<String, String>> nearByHospitalsList){
        for(int i = 0; i < nearByHospitalsList.size(); i++){
            Log.d("hospital "+i,nearByHospitalsList.get(i).toString());
            MarkerOptions markerOptions = new MarkerOptions();
            googleNearByHospital = nearByHospitalsList.get(i);
            Log.d("hospital info "+i,googleNearByHospital.get("place_name")+" "+googleNearByHospital.get("rating"));
            String hosp_name = "";
            String vicinity = "";
            String open_now = "";
            double rating = 0;
            double lng = 0;
            double lat = 0;

            hosp_name = googleNearByHospital.get("place_name");
            vicinity = googleNearByHospital.get("vicinity");
            open_now = googleNearByHospital.get("open_now");
            if(googleNearByHospital.get("rating")!= null || googleNearByHospital.get("lat")!= null || googleNearByHospital.get("lng")!= null){
                rating = Double.parseDouble(googleNearByHospital.get("rating"));
                lng = Double.parseDouble(googleNearByHospital.get("lat"));
                lat= Double.parseDouble(googleNearByHospital.get("lng"));
                LatLng currLoc = new LatLng(lat, lng);
                Log.d("current location from hosp",currLoc.toString());
                markerOptions.position(currLoc).title(hosp_name);
                String open = "";
                if(open_now.equals("true")){
                    open = "Yes";
                }else{
                    open = "No";
                }


                markerOptions.snippet("Open Now "+ open+"\nRating: "+rating);
                markerOptions.describeContents();
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.medicalloc));
                nMap.addMarker(markerOptions);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .zoom(10)                   // Sets the zoom
                        .tilt(45)
                        .target(currLoc)// Sets the tilt of the camera to 30 degrees
                        .build();
                nMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }else{
                i++;
            }


        }
    }

    public HashMap<String, String> getHospitals(){
        return googleNearByHospital;
    }

}
