package com.naitech.medicalLocator.POJOs;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Person_File implements Serializable {
    private String name;
    private String surname;
    private String id_number;
    private boolean gender;
    private String marital_status;
    private String language;
    private String dob;
    private String email;
    private String phoneNumber;
    private ArrayList<MedicalVisits> visits;

    public Person_File() {
    }

    public Person_File(String name, String surname, String id_number, boolean gender, String marital_status, String language, String dob, String email, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.id_number = id_number;
        this.gender = gender;
        this.marital_status = marital_status;
        this.language = language;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.visits = new ArrayList<>();
    }

    public ArrayList<MedicalVisits> getVisits() {
        return visits;
    }

    public void setVisits(ArrayList<MedicalVisits> visits) {
        this.visits = visits;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Name='" + name + '\'' +
                "\nSurname='" + surname + '\'' +
                "\nID ='" + id_number + '\'' +
                "\nGender=" + gender +
                "\nMarital Status='" + marital_status + '\'' +
                "\nLanguage='" + language + '\'' +
                "\nDate of Birth ='" + dob + '\'' +
                "\nEmail='" + email + '\'' +
                "\nPhone Number='" + phoneNumber + '\'' +
                "\nvisits=" + visits ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
