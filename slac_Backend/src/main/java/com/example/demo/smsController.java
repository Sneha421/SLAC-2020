package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.sql.*;

@RestController
@CrossOrigin
public class smsController
{
    Connection connection = null;
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlserver://slacdb.c3g9qdlqhwio.ap-south-1.rds.amazonaws.com;user=admin;password=slacDB2020;database=MedifyDB");
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @PostMapping(path = "/sendSMS")
    public void sendSMS(String phoneNumber, int positionNo) throws SQLException {
        int appointmentTime = 0;
        String hospitalName = "";
        String sql = "SELECT * FROM hospitalData";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next())
        {
            appointmentTime = resultSet.getInt("appointmentTime");
            hospitalName = resultSet.getString("hospitalName");
        }


        Twilio.init("ACe9da3a7a378288ac8b861de80838589d","4b7a5fdcc287d76107ed52d593aa7c4c");

        Message message = null;

        if(positionNo == 1)
        {
            message = Message.creator(
                    new com.twilio.type.PhoneNumber("+91"+phoneNumber),//To
                    new com.twilio.type.PhoneNumber("+12057782710"), //From
                    hospitalName+": You are next in line, please hurry!")
                    .create();
        }
        else
        {
            message = Message.creator(
                    new com.twilio.type.PhoneNumber("+91"+phoneNumber),//To
                    new com.twilio.type.PhoneNumber("+12057782710"), //From
                    hospitalName+": There are "+positionNo+" people ahead of you, you have approximately " +
                            (appointmentTime*positionNo)+" minutes left.Please be ready")
                    .create();
        }




        System.out.println(message.getSid());


    }
}
