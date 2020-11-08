package com.example.demo;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:63344")
public class queueController
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
    @GetMapping(path = "/getQueue")
    public List<queueModel> getQueue() throws SQLException
    {
        List<queueModel> queueModelList = new ArrayList<>();
        String queueQuery = "SELECT * FROM patientQueue2";
        Statement queueStatement = connection.createStatement();
        ResultSet queueResult = queueStatement.executeQuery(queueQuery);
        while (queueResult.next())
        {
            queueModel queueModel = new queueModel();

            queueModel.setPatientID(queueResult.getInt("patientID"));
            queueModel.setPatientName(queueResult.getString("patientName"));
            queueModel.setPhoneNumber(queueResult.getString("phoneNo"));

//            queueModel.setDate(queueResult.getDate("date"));
            queueModel.setTokeNumber(queueResult.getInt("token"));

/*            queueModel.setSpecification(queueResult.getString("spec"));
            queueModel.setToggle(queueResult.getString("toggle"));*/

            queueModelList.add(queueModel);
        }

        return queueModelList;
    }

    @PutMapping(path = "/postQueue")
    public @ResponseBody void postQueue(String patientName, int phoneNo, int tokenNo)
    {

        System.out.println(patientName);
        String submitQuery = "INSERT INTO patientQueue2"+
                "(patientName, phoneNo, tokenNo) " +
                "VALUES (?, ?, ?)";
        try
        {
            PreparedStatement submitStatement = connection.prepareStatement(submitQuery);
            submitStatement.setString(1, patientName);
            submitStatement.setInt(2, phoneNo);
            submitStatement.setInt(3, tokenNo);

            submitStatement.executeUpdate();
            submitStatement.close();
        }
        catch (SQLException submitError)
        {
            submitError.printStackTrace();
        }
    }
}
