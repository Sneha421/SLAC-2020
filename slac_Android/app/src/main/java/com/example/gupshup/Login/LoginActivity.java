package com.example.gupshup.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gupshup.Common.Util;
import com.example.gupshup.MainActivity;
import com.example.gupshup.Password.ResetPasswordActivity;
import com.example.gupshup.R;
import com.example.gupshup.SignUp.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity
{
    private EditText editEmail, editPassword;
    private String email, password;
    Button login;
    TextView resetPwdReDir;
    View progBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        resetPwdReDir = findViewById(R.id.resetPwd);
        progBar = findViewById(R.id.progressBar);

        resetPwdReDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                email = editEmail.getText().toString().trim();
                password = editPassword.getText().toString().trim();


                progBar.setVisibility(View.VISIBLE);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progBar.setVisibility(View.GONE);
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Login Failed: "+
                                    task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null)
        {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Util.updateDeviceToken(LoginActivity.this, instanceIdResult.getToken() );
                }
            });


            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }

    }

    public void SignUpReDir(View view)
    {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void Login(View view)
    {

     /*   if(email.isEmpty())
        {
            editEmail.setError("Email field is required!");
        }
        else if (password.isEmpty())
        {
            editPassword.setError("Password field is required!");
        }
        else
        {

        }*/
    }
}