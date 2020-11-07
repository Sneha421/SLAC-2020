package com.example.medify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import okhttp3.Call;
import okhttp3.FormBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HomeActivity extends AppCompatActivity
{
    Button btnSubmit;

    TextView patientAgeTV, patientConscTV, bloodLossTV, patientGenderTV, patientDescTV, accidentCategoryTV;

    EditText patientAgeET, patientDescET, accidentCategoryET;

    RadioGroup patientConscRG, bloodLossRG, patientGenderRG;

    RadioButton patientConscRB, bloodLossRB, patientGenderRB;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSubmit = findViewById(R.id.button);

        patientAgeET = findViewById(R.id.patientAgeET);
        accidentCategoryET = findViewById(R.id.accidentCategoryET);
        patientDescET = findViewById(R.id.patientDescET);

        patientConscRG = findViewById(R.id.patientConscRG);
        bloodLossRG = findViewById(R.id.bloodLossRG);
        patientGenderRG = findViewById(R.id.patientGenderRG);



        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String paramedicName = firebaseAuth.getCurrentUser().toString();

                String patientDesc = patientDescET.getText().toString();

                String patientAge = patientAgeET.getText().toString();

                String accidentCategory = accidentCategoryET.getText().toString();

                int selectedConsc = patientConscRG.getCheckedRadioButtonId();
                patientConscRB = (RadioButton) findViewById(selectedConsc);
                String patientConsc = patientConscRB.getText().toString();

                int selectedGender = patientGenderRG.getCheckedRadioButtonId();
                patientGenderRB = (RadioButton) findViewById(selectedGender);
                String patientGender = patientGenderRB.getText().toString();

                int selectedBloodLoss= bloodLossRG.getCheckedRadioButtonId();
                bloodLossRB = (RadioButton) findViewById(selectedBloodLoss);
                String bloodLoss = bloodLossRB.getText().toString();


                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            RequestBody formBody = new FormBody.Builder()
                                    .add("paramedicName", paramedicName)
                                    .add("patientDesc",patientDesc)
                                    .add("patientAge",patientAge)
                                    .add("patientConsc",patientConsc)
                                    .add("patientGender",patientGender)
                                    .add("accidentCategory",accidentCategory)
                                    .add("bloodLoss",bloodLoss)
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://192.168.174.1:8080" + "/submitForm")
                                    .post(formBody)
                                    .build();

                            OkHttpClient client = new OkHttpClient();

                            Call call = client.newCall(request);
                            Response response = call.execute();

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}