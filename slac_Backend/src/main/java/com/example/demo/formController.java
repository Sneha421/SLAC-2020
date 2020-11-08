package com.example.demo;


import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    @GetMapping(path = "/getForm")
    public @ResponseBody List<formModel> getForm() throws SQLException {
        List<formModel> formDataList = new ArrayList<>();
        String getQuery = "SELECT * FROM patientInfo";
        Statement getStatement = connection.createStatement();
        ResultSet formDataResult = getStatement.executeQuery(getQuery);
        while (formDataResult.next())
        {
            formModel formModel = new formModel();

            formModel.setFormID(formDataResult.getInt("formID"));
            formModel.setParamedicName(formDataResult.getString("paramedicName"));
            formModel.setPatientDesc(formDataResult.getString("patientDesc"));
            formModel.setPatientAge(formDataResult.getInt("patientAge"));
            formModel.setPatientConsc(formDataResult.getString("patientConsc"));
            formModel.setPatientGender(formDataResult.getString("patientGender"));
            formModel.setAccidentCategory(formDataResult.getString("accidentCategory"));
            formModel.setBloodLoss(formDataResult.getString("bloodLoss"));
            formDataList.add(formModel);
        }
        return formDataList;
    }

    @GetMapping(path = "/submitForm")
    public @ResponseBody String submitForm()
    {
        return "Hello World";
    }

    @PostMapping(path = "/submitForm")
    public @ResponseBody void submitForm(formModel formData)
    {
        System.out.println(formData.getParamedicName());
        String submitQuery = "INSERT INTO patientInfo"+
                "(paramedicName, patientAge, patientGender, patientConsc," +
                " accidentCategory, patientDesc, bloodLoss) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try
        {
            PreparedStatement submitStatement = connection.prepareStatement(submitQuery);
            submitStatement.setString(1, formData.getParamedicName());
            submitStatement.setInt(2, formData.getPatientAge());
            submitStatement.setString(3,formData.getPatientGender());
            submitStatement.setString(4, formData.getPatientConsc());
            submitStatement.setString(5, formData.getAccidentCategory());
            submitStatement.setString(6, formData.getPatientDesc());
            submitStatement.setString(7, formData.getBloodLoss());
            submitStatement.executeUpdate();
            submitStatement.close();
        }
        catch (SQLException submitError)
        {
            submitError.printStackTrace();
        }
    }

    @DeleteMapping(path = "/removeEntry")
    public @ResponseBody void removeEntry(int formID) throws SQLException
    {
        String delQuery = "DELETE from patientInfo where formID = "+formID;
        PreparedStatement delStatement = connection.prepareStatement(delQuery);

        try
        {
            delStatement.executeUpdate();
            delStatement.close();
        }
        catch (SQLException delError)
        {
            delError.printStackTrace();
        }

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
