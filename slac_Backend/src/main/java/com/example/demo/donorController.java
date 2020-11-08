package com.example.demo;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
@CrossOrigin
public class donorController
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
    @PostMapping(path = "/sendAlert")
    public @ResponseBody void sendAlert(String bloodGroup, String loc) throws SQLException {
        String sql = "select * from bloodDonors where bloodGroup="+bloodGroup;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next())
        {
            String phoneNo = resultSet.getString("donorNo");
            Twilio.init("ACe9da3a7a378288ac8b861de80838589d","4b7a5fdcc287d76107ed52d593aa7c4c");
            Message message = null;
            message = Message.creator(
                    new com.twilio.type.PhoneNumber("+91"+phoneNo),//To
                    new com.twilio.type.PhoneNumber("+12057782710"), //From
                    "We need "+bloodGroup+" at this location: "+loc+". Please do reach out if willing")
                    .create();
        }
    }
}
