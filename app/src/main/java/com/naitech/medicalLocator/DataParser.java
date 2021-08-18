package com.naitech.medicalLocator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson){
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String namePlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference= "";
        String rating = "";
        JSONArray weekvisits;
        boolean openNow = false;

        try {
            if(!googlePlaceJson.isNull("name")){
                namePlace = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity")){
                vicinity = googlePlaceJson.getString("vicinity");
            }
            if(!googlePlaceJson.getJSONObject("geometry").getJSONObject("location").isNull("lat")){
                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                Log.d("lat from parser",latitude);
            }
            if(!googlePlaceJson.getJSONObject("geometry").getJSONObject("location").isNull("lng")){
                longitude= googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                Log.d("lat long from parser",longitude);
            }
            if(!googlePlaceJson.getJSONObject("opening_hours").isNull("open_now")){
                openNow = googlePlaceJson.getJSONObject("opening_hours").getBoolean("open_now");
            }
            if(!googlePlaceJson.isNull("reference")){
                reference = googlePlaceJson.getString("reference");
            }
            if(!googlePlaceJson.isNull("rating")){
                rating = googlePlaceJson.getString("rating");
            }


           // weekvisits = googlePlaceJson.getJSONObject("opening_hours").getJSONArray("weekday_text");


            googlePlaceMap.put("place_name",namePlace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",longitude);
            googlePlaceMap.put("lng",latitude);
            googlePlaceMap.put("reference",reference);
            googlePlaceMap.put("rating",rating);
            googlePlaceMap.put("open_now",String.valueOf(openNow));
            Log.d("places info",googlePlaceMap.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearByHospitals(JSONArray jsonArray){
        int counter = jsonArray.length();
        List<HashMap<String, String>> nearByHospitalsList = new ArrayList<>();

        HashMap<String, String> nearByHospital = null;

        for(int i = 0; i < counter; i++){
            try {
                nearByHospital = getPlace((JSONObject) jsonArray.get(i));
                nearByHospitalsList.add(nearByHospital);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearByHospitalsList;

    }

    public List<HashMap<String, String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            Log.d("parsed results", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearByHospitals(jsonArray);
    }
}
