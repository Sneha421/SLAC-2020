package com.example.demo;

public class hospitalModel
{
    private String hospitalName;
    private int noPeople;
    private int appointmentTime;
    private int missedTurn;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getNoPeople() {
        return noPeople;
    }

    public void setNoPeople(int noPeople) {
        this.noPeople = noPeople;
    }

    public int getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(int appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getMissedTurn() {
        return missedTurn;
    }

    public void setMissedTurn(int missedTurn) {
        this.missedTurn = missedTurn;
    }
}
