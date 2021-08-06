package com.naitech.medicalLocator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naitech.medicalLocator.POJOs.MedicalVisits;
import com.naitech.medicalLocator.POJOs.Person_File;
import com.naitech.medicalLocator.POJOs.child;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class add extends Fragment {

    TextInputLayout fname, sname,idNumber;
    AutoCompleteTextView relationship,dob ,gender;
    ArrayList<String> relationshipOps = new ArrayList<>();
    ArrayList<String> genderOps = new ArrayList<>();
    String genderAdded="";
    String relationshipAdded="";
    Button btncreateAdd;
    child person;
    private String parentKey= "";
    AlertDialog.Builder alert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fname = view.findViewById(R.id.fNameAdd);
        sname = view.findViewById(R.id.SNameAdd);
        idNumber = view.findViewById(R.id.idNumberAdd);
        relationship = view.findViewById(R.id.relationshipAdd);
        dob = view.findViewById(R.id.dobAdd);
        gender = view.findViewById(R.id.genderAdd);
        btncreateAdd = view.findViewById(R.id.btnFileCreateAdd);
        alert = new AlertDialog.Builder(view.getContext());

        genderOps.add("Male");
        genderOps.add("Female");

        relationshipOps.add("Child");
        relationshipOps.add("Parent");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.item,genderOps);
        gender = (AutoCompleteTextView)view.findViewById(R.id.genderAdd);
        gender.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapterRel = new ArrayAdapter<>(view.getContext(),R.layout.item,relationshipOps);
        relationship = (AutoCompleteTextView)view.findViewById(R.id.relationshipAdd);
        relationship.setAdapter(arrayAdapterRel);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
                materialDateBuilder.setTitleText("SELECT DATE OF BIRTH");
                final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
                materialDatePicker.show(getFragmentManager(), "MATERIAL_DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        dob.setText(materialDatePicker.getHeaderText());
                    }
                });

                materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dob.setError("Date Required");
                    }
                });

            }
        });

        gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                genderAdded = parent.getItemAtPosition(position).toString();
            }
        });

        relationship.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                relationshipAdded = parent.getItemAtPosition(position).toString();
                if(relationshipAdded.equals("Child")){
                    idNumber.setEnabled(false);
                    person = new child();
                }else{
                    idNumber.setEnabled(true);
                    Person_File p = new Person_File();
                    person = (child)p;
                }
            }
        });

        btncreateAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fname.getEditText().getText().length() == 0){
                    fname.setError("Name Required");
                    fname.requestFocus();
                }else if(sname.getEditText().getText().length() ==0){
                    sname.setError("Surname Required");
                    sname.requestFocus();
                }else if(idNumber.isEnabled()){
                    if(idNumber.getEditText().getText().length() == 0){
                        idNumber.setError("ID Number/passport Required");
                        idNumber.requestFocus();
                    }
                }else if(dob.getText().length() < 0){
                    dob.setError("Date Required");
                    dob.requestFocus();
                }else{
                    if(person instanceof child){
                        person.setName(fname.getEditText().getText().toString());
                        person.setSurname(sname.getEditText().getText().toString());
                        person.setDob(dob.getText().toString());
                        if(genderAdded.equals("Male")){
                            person.setGender(true);
                        }else{
                            person.setGender(false);
                        }
                        person.setRelationship(relationshipAdded);
                    }

                    ArrayList<MedicalVisits> uservisits = new ArrayList<MedicalVisits>();
                    Date dateTime =new  Date();
                    String dateCreated = dateTime.toString();
                    uservisits.add(new MedicalVisits("0/0",dateCreated,"You have not seen a doctor yet and this part will show the symptoms",person.getName()+" this will be the prescription given by your doctor"));
                    person.setVisits(uservisits);


                    btncreateAdd.setEnabled(false);

                    Query query = FirebaseDatabase.getInstance().getReference().child("Medical Files").orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot data:snapshot.getChildren()){
                                parentKey= data.getKey();
                                Log.d("key of email",parentKey);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    alert.setMessage(person.toString()).setTitle("Information Confirmation")
                            .setCancelable(false)
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //add information to the database
                                    final ProgressDialog pd = new ProgressDialog(getContext(),ProgressDialog.THEME_HOLO_DARK);
                                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    pd.setTitle("Please Wait...");
                                    //pd.getWindow().getCurrentFocus();
                                    pd.show();
                                    HashMap userUpdate = new HashMap();

                                    userUpdate.put(person.getName(),person);
                                    Log.d("hashmap",userUpdate.toString());
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    Task task = database.getReference().child("Medical Files").child(parentKey).child(relationshipAdded).updateChildren(userUpdate);

                                    task.addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            pd.dismiss();
                                            getActivity().finish();
                                            Toast.makeText(getContext(),"Your file has been successfully created", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btncreateAdd.setEnabled(true);
                                    Toast.makeText(getContext(),"Please edit the incorrect information", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();

                }

                Log.d("Person created",person.toString());
            }
        });

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
