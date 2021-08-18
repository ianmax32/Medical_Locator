package com.naitech.medicalLocator.ui.send;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;

import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DottedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.naitech.medicalLocator.POJOs.MedicalVisits;
import com.naitech.medicalLocator.POJOs.Person_File;
import com.naitech.medicalLocator.POJOs.child;
import com.naitech.medicalLocator.R;
import com.naitech.medicalLocator.ui.home.HomeFragment;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SendFragment extends Fragment {

    private TextView textView;
    ArrayList<String> childrenList;

    String name="";
    String surname="";
    private String visitsInfo = "";
    private String feeling = "";
    Person_File person = new Person_File();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String key="";
    ArrayList<MedicalVisits> userVisits = new ArrayList<>();
    private ProgressDialog pd;
    private Button sendPDF;
    private String filePath ="";
    Bitmap bitmap;

    public static SendFragment newInstance(String visits){
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putString("visits",visits);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_send, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        textView = root.findViewById(R.id.allhistory);
        sendPDF = root.findViewById(R.id.sendpdf);

        pd= new ProgressDialog(getContext(),ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Getting All Visits...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        Log.d("visits from before",userVisits.toString());
        checkData();
        Log.d("textview info",textView.getText().toString());

        sendPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Generating PDF");
                //pd.show();
                Log.d("saveclicked","save file clicked");
                try {
                    if (ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        createPDF();
                        sendPDF.setEnabled(false);
                    }else{
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    private void createPDF() throws FileNotFoundException {
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File createdFile = new File(filePath, person.getName()+" MedicalFile.pdf");
        OutputStream outputStream = new FileOutputStream(createdFile);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Log.d("save files","created");

        pdfDoc.setDefaultPageSize(PageSize.A4);
        doc.setMargins(30,30,30,30);
        doc.setBorder(new DottedBorder(5));
        Drawable d = getResources().getDrawable(R.drawable.mainpic);


        /*StorageReference profilePicUser = FirebaseStorage.getInstance().getReference().child("Images/"+person.getEmail());
        profilePicUser.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    bitmap = BitmapFactory.decodeStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/

        bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);
        image.setWidth(100);
        image.setHeight(100);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph heading = new Paragraph(person.getName()+"'s Medical File").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);
        float[] width = {150f,250f};
        Table table = new Table(width);

        table.setHorizontalAlignment(HorizontalAlignment.CENTER);


        table.addCell(new Cell().add(new Paragraph("Name and Surname")));
        table.addCell(new Cell().add(new Paragraph(person.getName()+ " "+ person.getSurname())));

        table.addCell(new Cell().add(new Paragraph("Date of Birth")));
        table.addCell(new Cell().add(new Paragraph(person.getDob())));

        table.addCell(new Cell().add(new Paragraph("ID Number")));
        table.addCell(new Cell().add(new Paragraph(person.getId_number())));

        table.addCell(new Cell().add(new Paragraph("Phone Number")));
        table.addCell(new Cell().add(new Paragraph(person.getPhoneNumber())));

        table.addCell(new Cell().add(new Paragraph("Email")));
        table.addCell(new Cell().add(new Paragraph(person.getEmail())));

        table.addCell(new Cell().add(new Paragraph("Gender")));
        String gender = "";
        if(person.isGender()){
            gender = "Female";
        }else{
            gender = "Male";
        }
        table.addCell(new Cell().add(new Paragraph(gender)));

        BarcodeQRCode qrCode = new BarcodeQRCode(person.getId_number());
        PdfFormXObject qrCodeObject = qrCode.createFormXObject(Color.BLACK,pdfDoc);
        Image qrCodeImage = new Image(qrCodeObject).setWidth(80).setHorizontalAlignment(HorizontalAlignment.CENTER);
        Paragraph allresults = new Paragraph(textView.getText().toString());
        Paragraph feelings = new Paragraph(feeling);
        try {
            allresults.setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN)).setFontSize(13).setTextAlignment(TextAlignment.LEFT);
            feelings.setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN)).setFontSize(13).setTextAlignment(TextAlignment.LEFT);
        }catch (IOException e){
            Log.d("font exception",e.getMessage());
        }
        Log.d("paragraph",textView.getText().toString());



        doc.add(image);
        doc.add(heading);
        doc.add(new Paragraph("****************************************************************************************************************"));
        doc.add(table);
        doc.add(allresults);
        //doc.add(new Paragraph("How Have You been Feeling?").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER));
        //doc.add(feelings);
        doc.add(qrCodeImage);
        doc.close();
        pd.dismiss();
        Log.d("save files","added all information");
        //Toast.makeText(getContext(), "PDF Medical File ready at"+filePath, Toast.LENGTH_SHORT).show();


        File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+File.separator+(person.getName()+" MedicalFile.pdf"));
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        if(myFile.exists()){
            sendIntent.setType("application/pdf");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myFile));
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sendIntent,"Share Medical File"));

            //getActivity().onBackPressed();
        }else{
            Toast.makeText(getContext(), "File does not exist", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public void checkData(){
        Log.d("Checkdata","Called");
        Query query = database.getReference("Medical Files").orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data:snapshot.getChildren()){
                    key = data.getKey();
                    Log.d("key",key);
                    person.setName(data.getValue(Person_File.class).getName());
                    person.setSurname(data.getValue(Person_File.class).getSurname());
                    person.setId_number(data.getValue(Person_File.class).getId_number());
                    person.setDob(data.getValue(Person_File.class).getDob());
                    person.setMarital_status(data.getValue(Person_File.class).getMarital_status());
                    person.setEmail(data.getValue(Person_File.class).getEmail());
                    person.setPhoneNumber(data.getValue(Person_File.class).getPhoneNumber());
                    getFeelings();
                    getVisits();

                }

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
                visitsInfo = "";
                for(DataSnapshot data:snapshot.getChildren()){
                    MedicalVisits visit = data.getValue(MedicalVisits.class);
                    Log.d("user data from send",visit.toString());
                    userVisits.add(visit);
                }
                Log.d("list now",String.valueOf(userVisits.size()));
                userVisits.add(new MedicalVisits("test1","Thursday, 05 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test2","Saturday, 30 August 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test3","Friday, 30 September 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test4","Monday, 30 October 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test5","Wednesday, 30 November 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test6","Tuesady, 30 December 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test7","Thursday, 30 January 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test8","Friday, 30 February 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));
                userVisits.add(new MedicalVisits("test9","Saturday, 30 October 2021","Fever or chills · Cough · Shortness of breath or difficulty breathing · Fatigue · Muscle or body aches · Headache","a written direction or order for the preparing and use","37 Deg","This is the diagnosis","7.8 mmol/L","Ian Masaga","4512781"));

                for(MedicalVisits v:userVisits){
                    visitsInfo+=v.toString() + "\n";
                }

                pd.dismiss();
                textView.setText(visitsInfo+ "\n\nHow Have you been feeling? \n"+ feeling+ "\n\n");
                Log.d("textview info",textView.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("user visits from send",userVisits.toString());
    }


    public void getFeelings(){
        Log.d("feelings","Called");
        if(name.equals("CURRENT USER")){
            checkData();
        }else{
            Query query = database.getReference("Medical Files").child(key).child("Feeling");
            feeling="";
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data:snapshot.getChildren()){
                        feeling+="On "+data.getKey()+ " : " + data.getValue()+"\n";
                    }
                    feeling+="\n\n\n";
                    Log.d("feelingsupdate",feeling);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}