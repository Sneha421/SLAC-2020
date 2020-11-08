package com.example.demo;

import org.apache.commons.codec.StringDecoder;

public class donorModel
{
    private int donorID;
    private String donorName;
    private int donorAge;
    private String donorNo;
    private String bloodGroup;

    public int getDonorID() {
        return donorID;
    }

    public void setDonorID(int donorID) {
        this.donorID = donorID;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public int getDonorAge() {
        return donorAge;
    }

    public void setDonorAge(int donorAge) {
        this.donorAge = donorAge;
    }

    public String getDonorNo() {
        return donorNo;
    }

    public void setDonorNo(String donorNo) {
        this.donorNo = donorNo;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}
