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

        try {
            if(!googlePlaceJson.isNull("name")){
                namePlace = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity")){
                namePlace = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",namePlace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);
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
