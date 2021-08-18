package com.naitech.medicalLocator;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naitech.medicalLocator.POJOs.MedicalVisits;
import com.naitech.medicalLocator.POJOs.Person_File;
import com.naitech.medicalLocator.ui.home.HomeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_Personal_Info extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AutoCompleteTextView gender;
    private AutoCompleteTextView language , maritalStatus;
    private String personGender = "";
    private String languageSpeak = "";
    private String mStatus = "";
    private Button btnFileCreate;
    private TextInputLayout fname, sname,idNumber, phoneNumber, occupation;
    private AutoCompleteTextView dob;
    private AlertDialog.Builder alert;
    private ImageButton profileImage;
    private int SELECT_IMAGE = 1;
    private ProgressBar simpleProgressBar;
    private String key = "";
    private Person_File person = new Person_File();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Uri profilePic;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public fragment_Personal_Info() {
        // Required empty public constructor

    }


    // TODO: Rename and change types and number of parameters
    public static fragment_Personal_Info newInstance(String param1, String param2) {
        fragment_Personal_Info fragment = new fragment_Personal_Info();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__personal__info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> options = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        List<String> maritalStatusOp = new ArrayList<>();
        gender =  view.findViewById(R.id.gender);
        language = view.findViewById(R.id.language);
        maritalStatus = view.findViewById(R.id.maritalStatus);
        btnFileCreate = view.findViewById(R.id.btnFileCreate);
        profileImage = view.findViewById(R.id.profileImage);



        ArrayList<Person_File> userInfo = (ArrayList<Person_File>)getActivity().getIntent().getBundleExtra("userBundle").getSerializable("userInfo");

        fname = view.findViewById(R.id.fName);
        sname = view.findViewById(R.id.SName);
        if(TextUtils.isEmpty(userInfo.get(0).getName()) || TextUtils.isEmpty(userInfo.get(0).getSurname())){
            fname.getEditText().setText(userInfo.get(0).getName());
            sname.getEditText().setText(userInfo.get(0).getSurname());
            fname.setEnabled(false);
            sname.setEnabled(false);
        }
        idNumber = view.findViewById(R.id.idNumber);
        phoneNumber= view.findViewById(R.id.phoneNumber);
        occupation = view.findViewById(R.id.occupation);
        dob = view.findViewById(R.id.dob);
        alert = new AlertDialog.Builder(view.getContext());

        options.add("Male");
        options.add("Female");

        maritalStatusOp.add("Married");
        maritalStatusOp.add("Single");

        languages.add("Afrikaans");
        languages.add("English");
        languages.add("Ndebele");
        languages.add("Xhosa");
        languages.add("Zulu");
        languages.add("Sotho sa Leboa");
        languages.add("Sotho");
        languages.add("Tswana");
        languages.add("Swati");
        languages.add("Venda");
        languages.add("Tsonga");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.item,options);
        gender = (AutoCompleteTextView)view.findViewById(R.id.gender);
        gender.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapterLanguage = new ArrayAdapter<>(view.getContext(),R.layout.item,languages);
        language = (AutoCompleteTextView)view.findViewById(R.id.language);
        language.setAdapter(arrayAdapterLanguage);

        ArrayAdapter<String> arrayAdapterMStatus= new ArrayAdapter<>(view.getContext(),R.layout.item,maritalStatusOp);
        maritalStatus = (AutoCompleteTextView)view.findViewById(R.id.maritalStatus);
        maritalStatus.setAdapter(arrayAdapterMStatus);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
                }

            }

        });

        dob = view.findViewById(R.id.dob);
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
                personGender = parent.getItemAtPosition(position).toString();
            }
        });

        language.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                languageSpeak = parent.getItemAtPosition(position).toString();
            }
        });

        maritalStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mStatus = parent.getItemAtPosition(position).toString();
            }
        });


        btnFileCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fname.getEditText().getText().length() == 0){
                    fname.setError("Name Required");
                    fname.requestFocus();
                }else if(sname.getEditText().getText().length() ==0){
                    sname.setError("Surname Required");
                    sname.requestFocus();
                }else
                if(idNumber.getEditText().getText().length() == 0){
                    idNumber.setError("ID Number/passport Required");
                    idNumber.requestFocus();
                }else if(phoneNumber.getEditText().getText().length() == 0){
                    phoneNumber.setError("Cell Number Required");
                    phoneNumber.requestFocus();
                }if(occupation.getEditText().getText().length() == 0){
                    occupation.setError("What do you do?");
                    occupation.requestFocus();
                }if(dob.getText().length() < 0){
                    dob.setError("Date Required");
                    dob.requestFocus();
                }/*if(profileImage.getTag().equals("R.drawable.userdefaultpic")){
                    profileImage.requestFocus();
                }*/else{
                    person.setName(fname.getEditText().getText().toString());
                    person.setSurname(sname.getEditText().getText().toString());
                    person.setId_number(idNumber.getEditText().getText().toString());
                    person.setLanguage(languageSpeak);
                    person.setMarital_status(mStatus);
                    person.setPhoneNumber(phoneNumber.getEditText().getText().toString());
                    person.setDob(dob.getText().toString());
                    person.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    ArrayList<MedicalVisits> uservisits = new ArrayList<MedicalVisits>();
                    Date dateTime =new  Date();
                    String dateCreated = dateTime.toString();
                    uservisits.add(new MedicalVisits("0/0",dateCreated,person.getName()+" you have not seen a doctor yet and this is shows the symptoms","This will be the prescription","This the temp on the day of visit","Blood gluecose is shown here","Final Diagnosis is added here","Doc Name","Practice Number"));
                    person.setVisits(uservisits);

                    if(personGender.equals("Male")){
                        person.setGender(true);
                    }else{
                        person.setGender(false);
                    }
                    btnFileCreate.setEnabled(false);

                    Query query = FirebaseDatabase.getInstance().getReference().child("Medical Files").orderByChild("email").equalTo(person.getEmail());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot data:snapshot.getChildren()){
                                key = data.getKey();
                                Log.d("key of email",key);
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
                                    pd.setCanceledOnTouchOutside(false);
                                    pd.setCancelable(false);
                                    pd.show();
                                    HashMap userUpdate = new HashMap();

                                    userUpdate.put(key,person);
                                    Log.d("hashmap",userUpdate.toString());
                                    StorageReference mStorageRef = storage.getReference();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference().child("Medical Files").child(key).setValue(person).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            StorageReference profilePics = mStorageRef.child("Images/"+person.getEmail());
                                            profilePics.putFile(profilePic)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                                            getActivity().onBackPressed();
                                                            Toast.makeText(getContext(),"Your file has been successfully created", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            pd.dismiss();
                                                        }
                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                    double progressPercent = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                                                    pd.setMessage("Progress: " + (int)progressPercent + "%");
                                                }
                                            });
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnFileCreate.setEnabled(true);
                                    Toast.makeText(getContext(),"Please edit the incorrect information", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();

                }

                Log.d("Person created",person.toString());
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        profilePic = data.getData();
                        //sImage = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
                        Glide.with(getContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(profileImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
