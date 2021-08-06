package com.naitech.medicalLocator.POJOs;

public class MedicalVisits {
    private String bp;
    private String date;
    private String symptoms;
    private String prescription;

    public MedicalVisits() {
    }

    public MedicalVisits(String bp, String date, String symptoms, String prescription) {
        this.bp = bp;
        this.date = date;
        this.symptoms = symptoms;
        this.prescription = prescription;
    }

    public <T> MedicalVisits(T visits) {
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    @Override
    public String toString() {
        return
                "BLOOD PRESSURE :'" + bp + '\'' +
                "\n\nDATE :'" + date + '\'' +
                "\n\nSYMPTOMS :'" + symptoms + '\'' +
                "\n\nPRESCRIPTION :'" + prescription ;
    }
}
