package com.example.demo;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
@CrossOrigin
public class formController
{
    Connection connection;
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlserver://slacdb.c3g9qdlqhwio.ap-south-1.rds.amazonaws.com;user=admin;password=slacDB2020;database=MedifyDB");
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @PostMapping(path = "/submitForm")
    public @ResponseBody void submitForm(formModel formData)
    {
        System.out.println(formData);
       /* String submitQuery = "INSERT INTO patientInfo"+
                "(formID, paramedicName, patientAge, patientGender, patientConsc," +
                " accidentCategory, accidentDesc, bloodLossRange) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try
        {
            PreparedStatement submitStatement = connection.prepareStatement(submitQuery);
            submitStatement.setInt(1, formData.getFormID());
            submitStatement.setString(2, formData.getParamedicName());
            submitStatement.setInt(3, formData.getPatientAge());
            submitStatement.setString(4,formData.getPatientGender());
            submitStatement.setString(5, formData.getPatientConsc());
            submitStatement.setString(6, formData.getAccidentCategory());
            submitStatement.setString(7, formData.getAccidentDesc());
            submitStatement.setString(8, formData.getBloodLossRange());
            submitStatement.executeUpdate();
            submitStatement.close();
        }
        catch (SQLException submitError)
        {
            submitError.printStackTrace();
        }*/
    }

/*    String sql = "update  product  set Name = ?, Prodgroup= ?, Inventory= ?, onorder=? where id=?";
        try {
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, product.getName());
    ps.setString(2, product.getProdgroup());
    ps.setInt(3, product.getInventory());
    ps.setInt(4, product.getonorder());
    ps.setInt(5, product.getId());


    String sql2 = "SELECT * from product where id= "+product.getId();
    Statement stm = conn.createStatement();
    ResultSet resultSet = stm.executeQuery(sql2);
    while (resultSet.next())
    {
        idOld = resultSet.getInt("id");
        NameOld = resultSet.getString("Name");
        ProdGroupOld = resultSet.getString("Prodgroup");
        InventoryOld = resultSet.getInt("Inventory");
        OnOrderOld = resultSet.getInt("onorder");
    }
    ps.executeUpdate();
    ps.close();*/
}
