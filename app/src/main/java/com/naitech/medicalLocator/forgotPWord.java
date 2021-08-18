package com.naitech.medicalLocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPWord extends AppCompatActivity {

    Button  createnewpword;
    FirebaseAuth auth;
    TextInputLayout emailchangepword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pword);

        createnewpword = findViewById(R.id.btnForgot);
        auth = FirebaseAuth.getInstance();
        emailchangepword = findViewById(R.id.emailChangePassword);

        createnewpword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String email = emailchangepword.getEditText().getText().toString().trim();

        if(email.isEmpty()){
            emailchangepword.setError("Email Required");
            emailchangepword.requestFocus();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Log.d("email",String.valueOf(Patterns.EMAIL_ADDRESS.matcher(email).matches()));
            emailchangepword.setError("Please provide a valid email!");
            emailchangepword.requestFocus();
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Check your email to reset your password", Toast.LENGTH_LONG).show();
                    onBackPressed();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Try again something happened", Toast.LENGTH_LONG).show();
                    emailchangepword.getEditText().setText("");
                    emailchangepword.requestFocus();
                }
            }
        });
    }
}
