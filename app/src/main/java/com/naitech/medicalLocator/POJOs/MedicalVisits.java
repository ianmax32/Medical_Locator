package com.naitech.medicalLocator.POJOs;

public class MedicalVisits {
    private String bp;
    private String date;
    private String symptoms;
    private String prescription;
    private String temperature;
    private String bloodGluecose;
    private String diagnosis;
    private String doctorName;
    private String doct_practiceNum;


    public MedicalVisits() {
    }

    public MedicalVisits(String bp, String date, String symptoms, String prescription, String temperature, String bloodGluecose, String diagnosis, String doctorName, String doct_practiceNum) {
        this.bp = bp;
        this.date = date;
        this.symptoms = symptoms;
        this.prescription = prescription;
        this.temperature = temperature;
        this.bloodGluecose = bloodGluecose;
        this.diagnosis = diagnosis;
        this.doctorName = doctorName;
        this.doct_practiceNum = doct_practiceNum;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoct_practiceNum() {
        return doct_practiceNum;
    }

    public void setDoct_practiceNum(String doct_practiceNum) {
        this.doct_practiceNum = doct_practiceNum;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBloodGluecose() {
        return bloodGluecose;
    }

    public void setBloodGluecose(String bloodGluecose) {
        this.bloodGluecose = bloodGluecose;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
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
        return  "Doctor :'" + doctorName + '\'' + " P-Number: "+doct_practiceNum+
                "\nDate:'" + date + '\'' + "\nBlood Pressure:'" + bp + '\'' +
                "\nSymptoms:'" + symptoms + '\'' +
                "\nPrescription:'" + prescription + '\'' +
                "\nTemperature:'" + temperature + '\'' +
                "\nBlood Sugar:'" + bloodGluecose + '\'' +
                "\nDiagnosis='" + diagnosis + '\'' +
                "\n"+"\n";
    }
}
