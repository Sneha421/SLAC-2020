package com.ailadder.rest.webservices.restfulwebservices;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class EmployeeController
{
    Connection connection;
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;user=sa;password=9@0Gandhinagaram5@3;database=BasicEmpDB");
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @GetMapping(path = "/getAllEmp")
    public @ResponseBody List<EmployeeModel> getAllEmp() throws SQLException
    {
        List<EmployeeModel> employees = new ArrayList<>();

        String sql = "SELECT * from Employee";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next())
        {
            EmployeeModel employeeModel = new EmployeeModel();
            employeeModel.setID(resultSet.getInt("ID"));
            employeeModel.setName(resultSet.getString("Name"));
            employees.add(employeeModel);
        }

        return employees;
    }
}
