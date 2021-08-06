package com.naitech.medicalLocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.naitech.medicalLocator.POJOs.Person_File;
import com.naitech.medicalLocator.R;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private MaterialButton register;
    private MaterialButton loginFromReg;
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout nameReg;
    TextInputLayout surnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        register = findViewById(R.id.btnRegister);
        loginFromReg = findViewById(R.id.btnLoginFromRegister);
        email = findViewById(R.id.usernameReg);
        password = findViewById(R.id.passwordReg);
        nameReg = findViewById(R.id.regName);
        surnReg = findViewById(R.id.regSurname);
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        loginFromReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserLogin.class));
                finish();
            }
        });
    }

    private void createUser(){
        String emailFromUser = email.getEditText().getText().toString().trim();

        String passwordFromUser = password.getEditText().getText().toString().trim();
        String nameFromUser = nameReg.getEditText().getText().toString().trim();
        String surnameFromUser = surnReg.getEditText().getText().toString().trim();

        if(TextUtils.isEmpty(emailFromUser)){
            email.setError("Email cannot be empty");
            email.requestFocus();
        }else if(TextUtils.isEmpty(passwordFromUser) || passwordFromUser.length() < 6){
            password.setError("Password cannot have less than 6 characters");
            password.requestFocus();
        }else if(TextUtils.isEmpty(nameFromUser)){
            nameReg.setError("Name Required");
            nameReg.requestFocus();
        }else if(TextUtils.isEmpty(surnameFromUser)){
            surnReg.setError("Surname required");
            surnReg.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(emailFromUser,passwordFromUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Person_File person = new Person_File();
                        person.setName(nameFromUser);
                        person.setSurname(surnameFromUser);
                        person.setEmail(emailFromUser);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        HashMap<String, Person_File> user = new HashMap<>();
                        user.put(emailFromUser,person);
                        database.getReference().child("Medical Files").push().setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"User Successfully Registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserLogin.class));
                                finish();
                            }
                        });

                    }else{
                        Log.d("task",task.getException().toString());
                        Toast.makeText(getApplicationContext(),"Registration Failed Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
