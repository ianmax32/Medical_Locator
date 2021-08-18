package com.naitech.medicalLocator;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naitech.medicalLocator.POJOs.Hospital;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ListOfHospitals extends AppCompatActivity {

    RecyclerView recycler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_hospitals);

        recycler = findViewById(R.id.recycler_view);
        //Log.d("size before loop",String.format("%s",hospitals.size()))
        Bundle bundle = this.getIntent().getExtras();
        HashMap<String,String> hospitals =(HashMap<String,String>)bundle.getSerializable("hospitals");
        Log.d("list of hospitals size",String.format("%s",hospitals.size()));
        Log.d("list of hospitals",hospitals.toString());
        /*recycler_view_adapter adapter= new recycler_view_adapter(this,hospitals);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
*/

    }



}
