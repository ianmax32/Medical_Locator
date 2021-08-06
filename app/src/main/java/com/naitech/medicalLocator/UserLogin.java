package com.naitech.medicalLocator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Timer;
import java.util.TimerTask;

public class UserLogin extends AppCompatActivity {
    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGNIN = 1;

    Button loginbtn;
    Button registeruser;
    FirebaseAuth mAuthLogin;
    TextInputLayout username;
    TextInputLayout password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        getSupportActionBar().hide();


        signInButton = findViewById(R.id.sign_in_button);
        loginbtn = findViewById(R.id.btnLogin);
        registeruser = findViewById(R.id.btnRegister);
        username =  findViewById(R.id.username);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registeruser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void loginUser(){
        String emailFromUser = username.getEditText().getText().toString().trim();
        String passwordFromUser = password.getEditText().getText().toString().trim();
        Log.d("user",emailFromUser+" "+passwordFromUser);
        if(TextUtils.isEmpty(emailFromUser)){
            username.setError("Email cannot be empty");
            username.requestFocus();
        }else if(TextUtils.isEmpty(passwordFromUser)){
            password.setError("Password cannot be empty");
            password.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(emailFromUser,passwordFromUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"User Logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Login failed Username or Password Wrong", Toast.LENGTH_LONG).show();
                        password.getEditText().setText("");
                        username.getEditText().setText("");
                        username.requestFocus();
                    }
                }
            });
        }
    }

    private void signIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGNIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGNIN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("Task", task.getResult().toString());
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            Log.d("Task", task.getResult(ApiException.class).toString());
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(getApplicationContext(),"Signed In Sucessfully!", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch(ApiException e){
            Toast.makeText(getApplicationContext(),"Signed In Failed!", Toast.LENGTH_SHORT).show();
            Log.d("sign in",e.toString());
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    Intent mainActivityIntent = new Intent(UserLogin.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user){
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acc!= null){
            String email = acc.getEmail();
            String name = acc.getDisplayName();
            Uri profile = acc.getPhotoUrl();
            Toast.makeText(getApplicationContext(),"Successful sign in as " + email, Toast.LENGTH_SHORT).show();
        }
    }
}
