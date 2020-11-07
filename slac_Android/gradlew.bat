package com.example.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class RegBloodBankPage extends AppCompatActivity
{
    String beginning = "jdbc:mysql",ip = "192.168.1.104",port = "3306",db = "bloodbankappdb",un = "root",pw = "MariaDB9305";
    String url = beginning + "://" + ip + ":" + port + "/" + db;
    Button regBloodBank;
    EditText BBName, City, ContactNo, Address, PostalCode;
    Intent goToBloodBankHome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences bloodBankRegPageSet = getSharedPreferences("bloodBankRegSet",MODE_PRIVATE);
        String bloodBankRegPageCheck = bloodBankRegPageSet.getString("bloodBankRegPageEditor","");
        if(bloodBankRegPageCheck.equals("true"))
        {
            Intent goBBankPage = new Intent();
            goBBankPage.setClass(this,BloodBankHomePage.class);
            startActivity(goBBankPage);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_blood_bank_page);

        regBloodBank = findViewById(R.id.bloodBankPageReg);
        BBName = findViewById(R.id.bloodBankNameFill);
        City = findViewById(R.id.cityFill);
        ContactNo = findViewById(R.id.contactFill);
        Address = findViewById(R.id.addFill);
        PostalCode = findViewById(R.id.postalCodeFill);

        //

        goToBloodBankHome = new Intent();
        goToBloodBankHome.setClass(this,BloodBankHomePage.class);

        //

        regBloodBank.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new Insert().execute();
                Toast