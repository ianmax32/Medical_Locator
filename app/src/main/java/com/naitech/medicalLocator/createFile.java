package com.naitech.medicalLocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class createFile extends AppCompatActivity {

    fragment_Personal_Info fPersonalInfo;
    Medical_aid fMedicalAid;
    Person_Responsible fPersonRes;
    Next_of_kin fNextOfKin;
    List<String> options;
    Button btnCreateFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);
        getSupportActionBar().hide();

        Spinner spinnerFileOptions = findViewById(R.id.fileOptions);
        fPersonalInfo = new fragment_Personal_Info();
        fMedicalAid = new Medical_aid();
        fPersonRes = new Person_Responsible();
        fNextOfKin = new Next_of_kin();
        btnCreateFile = findViewById(R.id.btnCreateFile);

        options = new ArrayList<>();
        options.add("Personal Details");
        //options.add("Personal Responsible");
        //options.add("Medical Aid");
        //options.add("Next of Kin");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(createFile.this,R.layout.item,options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFileOptions.setAdapter(arrayAdapter);

        spinnerFileOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: selectFragment(fPersonalInfo);break;
                    //case 1: selectFragment(fPersonRes);break;
                    //case 2: selectFragment(fMedicalAid);break;
                    //case 3: selectFragment(fNextOfKin);break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //check if everything has been entered

    }

    private void selectFragment(Fragment frag){
        FragmentTransaction fregmentTransaction = getSupportFragmentManager().beginTransaction();
        fregmentTransaction.replace(R.id.frameLayout,frag);
        fregmentTransaction.commit();
    }
}
