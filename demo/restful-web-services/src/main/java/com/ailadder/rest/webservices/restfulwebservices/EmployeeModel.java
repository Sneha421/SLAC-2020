package com.ailadder.rest.webservices.restfulwebservices;

public class EmployeeModel
{
    private int ID;

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;

}
