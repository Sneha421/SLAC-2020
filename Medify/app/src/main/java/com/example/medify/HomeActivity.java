package com.example.medify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnSubmit = findViewById(R.id.button);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            RequestBody formBody = new FormBody.Builder()
                                    .add("formID", "3")
                                    .add("paramedicName", "para3@gmail.com")
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