package com.naitech.medicalLocator;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearByHospitals extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;
    private GoogleMap nMap;

    @Override
    protected String doInBackground(Object... objects) {
        nMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlaceData = downloadURL.readUrl(url);
            Log.d("google places url",googlePlaceData);
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
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googleNearByHospital = nearByHospitalsList.get(i);
            String hosp_name = googleNearByHospital.get("place_name");
            String vicinity = googleNearByHospital.get("vicinity");
            double lat = Double.parseDouble(googleNearByHospital.get("lat"));
            double lng = Double.parseDouble(googleNearByHospital.get("lng"));
            //String reference  = googleNearByHospital.get("place_name");

            LatLng currLoc = new LatLng(lat, lng);
            markerOptions.position(currLoc).title(hosp_name + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            nMap.addMarker(markerOptions);
            nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 15));
        }
    }
}
