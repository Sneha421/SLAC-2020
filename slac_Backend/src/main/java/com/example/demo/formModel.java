package com.example.demo;


public class formModel
{
    private int formID;
    private String paramedicName;
    private int patientAge;
    private String patientGender;
    private String patientConsc;

    public int getFormID() {
        return formID;
    }

    public void setFormID(int formID) {
        this.formID = formID;
    }

    public String getParamedicName() {
        return paramedicName;
    }

    public void setParamedicName(String paramedicName) {
        this.paramedicName = paramedicName;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientConsc() {
        return patientConsc;
    }

    public void setPatientConsc(String patientConsc) {
        this.patientConsc = patientConsc;
    }

    public String getAccidentCategory() {
        return accidentCategory;
    }

    public void setAccidentCategory(String accidentCategory) {
        this.accidentCategory = accidentCategory;
    }

    public String getAccidentDesc() {
        return accidentDesc;
    }

    public void setAccidentDesc(String accidentDesc) {
        this.accidentDesc = accidentDesc;
    }

    public String getBloodLossRange() {
        return bloodLossRange;
    }

    public void setBloodLossRange(String bloodLossRange) {
        this.bloodLossRange = bloodLossRange;
    }

    private String accidentCategory;
    private String accidentDesc;
    private String bloodLossRange;


}
