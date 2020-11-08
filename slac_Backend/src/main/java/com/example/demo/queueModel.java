package com.example.demo;

import java.sql.Date;

public class queueModel
{
    private int patientID;
    private String patientName;
    private String phoneNumber;
//    private Date date;
    private int tokeNumber;
/*    private String specification;
    private String toggle;*/

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

/*
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
*/

    public int getTokeNumber() {
        return tokeNumber;
    }

    public void setTokeNumber(int tokeNumber) {
        this.tokeNumber = tokeNumber;
    }

/*    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getToggle() {
        return toggle;
    }

    public void setToggle(String toggle) {
        this.toggle = toggle;
    }*/
}
