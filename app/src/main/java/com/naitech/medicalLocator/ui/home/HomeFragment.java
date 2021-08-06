package com.naitech.medicalLocator.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naitech.medicalLocator.POJOs.MedicalVisits;
import com.naitech.medicalLocator.POJOs.Person_File;
import com.naitech.medicalLocator.POJOs.child;
import com.naitech.medicalLocator.R;
import com.naitech.medicalLocator.createFile;
import com.naitech.medicalLocator.file_adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button btnCreateFile;
    private RecyclerView visitsRecyclerView;
    ArrayList<MedicalVisits> userVisits = new ArrayList<>();
    private file_adapter.RecyclerViewClickListener listener;
    AutoCompleteTextView selectUser;
    ArrayList<String> childrenList;

    String name="";
    String surname="";
    ArrayList<Person_File> person = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String key="";
    private file_adapter adapter;
    TextInputLayout textUser;
    private ProgressDialog pd;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        btnCreateFile = root.findViewById(R.id.btnCreateFile);
        visitsRecyclerView = root.findViewById(R.id.homeListRecyclerView);
        selectUser = root.findViewById(R.id.selectedUser);
        textUser = root.findViewById(R.id.textInputLayout);
        childrenList = new ArrayList<>();
        pd= new ProgressDialog(getContext(),ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Getting information...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //pd.getWindow().getCurrentFocus();
        pd.show();
        checkData();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(root.getContext(),R.layout.item,childrenList);
        selectUser = (AutoCompleteTextView)root.findViewById(R.id.selectedUser);
        selectUser.setAdapter(arrayAdapter);


        //selectUser.setListSelection(0);

        selectUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("selected user",parent.getItemAtPosition(position).toString());
                pd.show();
                getVisits(parent.getItemAtPosition(position).toString());
            }
        });



        Log.d("list",userVisits.toString());
        listener = new file_adapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                View report = getActivity().getLayoutInflater().inflate(R.layout.report,null);
                TextView date = report.findViewById(R.id.datereport);
                TextView bp = report.findViewById(R.id.bpreport);
                TextView symptoms = report.findViewById(R.id.symptomsreport);
                TextView prescribed  = report.findViewById(R.id.prescriptionreport);

                date.setText(userVisits.get(position).getDate().substring(0,10));
                bp.setText(userVisits.get(position).getBp());
                symptoms.setText(userVisits.get(position).getSymptoms());
                prescribed.setText(userVisits.get(position).getPrescription());
                MaterialAlertDialogBuilder visitInfo = new MaterialAlertDialogBuilder(getContext(),R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
                visitInfo.setTitle("CHECK UP");
                visitInfo.setIcon(R.drawable.ic_report_24dp)
                        .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setView(report);
                visitInfo.show();
            }
        };
        adapter= new file_adapter(getContext(),userVisits,listener);
        visitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        visitsRecyclerView.setAdapter(adapter);



        btnCreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), createFile.class);
                Bundle args = new Bundle();
                args.putSerializable("userInfo",(Serializable)person);
                intent.putExtra("userBundle",args);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case 10:
                item.setIcon(R.drawable.ic_menu_share);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                MedicalVisits send = userVisits.get(item.getGroupId());
                sendIntent.putExtra(Intent.EXTRA_TEXT, send.toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Share Contact");
                startActivity(shareIntent);
                return true;
                default:return super.onContextItemSelected(item);
        }
    }

    public void checkData(){
        Log.d("Checkdata","Called");
        Query query = database.getReference("Medical Files").orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                childrenList.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    key = data.getKey();
                    Log.d("key",key);
                    if(data.getValue(Person_File.class).getId_number()== null){
                        btnCreateFile.setVisibility(View.VISIBLE);
                        person.add(data.getValue(Person_File.class));
                        pd.dismiss();
                    }else if(data.child("Child").hasChildren()){
                        child child1 = new child();
                        HashMap<String,child> children = (HashMap<String, child>) data.child("Child").getValue();

                        Log.d("children",children.toString());
                        Log.d("children count",String.valueOf(data.child("Child").getChildrenCount()));
                        textUser.setVisibility(View.VISIBLE);
                        childrenList.add("CURRENT USER");
                        for(String key: children.keySet()){
                            childrenList.add(key);
                        }
                        getVisits();
                        pd.dismiss();
                    }else{
                        getVisits();

                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void getVisits(){
        Log.d("getvisits","Called");
        Query query = database.getReference("Medical Files").child(key).child("visits");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("number of visits",String.valueOf(snapshot.getChildrenCount()));
                userVisits.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    btnCreateFile.setVisibility(View.INVISIBLE);
                    visitsRecyclerView.setVisibility(View.VISIBLE);
                    MedicalVisits visit = data.getValue(MedicalVisits.class);
                    Log.d("user data",visit.toString());
                    userVisits.add(visit);
                }
                Log.d("list now",String.valueOf(userVisits.size()));
                userVisits.add(new MedicalVisits("test1","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test2","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test3","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test4","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test5","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test6","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test7","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test8","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                userVisits.add(new MedicalVisits("test9","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));

                pd.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getVisits(String name){
        Log.d("getvisits","Called");
        if(name.equals("CURRENT USER")){
            checkData();
        }else{
            Query query = database.getReference("Medical Files").child(key).child("Child").child(name).child("visits");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("number of visits",String.valueOf(snapshot.getChildrenCount()));
                    userVisits.clear();
                    for(DataSnapshot data:snapshot.getChildren()){
                        btnCreateFile.setVisibility(View.INVISIBLE);
                        visitsRecyclerView.setVisibility(View.VISIBLE);
                        MedicalVisits visit = data.getValue(MedicalVisits.class);
                        Log.d("user data "+name,visit.toString());
                        userVisits.add(visit);
                    }
                    Log.d("list now",String.valueOf(userVisits.size()));
                    userVisits.add(new MedicalVisits("test1","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test2","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test3","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test4","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test5","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test6","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test7","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test8","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    userVisits.add(new MedicalVisits("test9","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use"));
                    pd.dismiss();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        checkData();
    }
}