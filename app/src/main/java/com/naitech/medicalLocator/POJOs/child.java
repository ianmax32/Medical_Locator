package com.naitech.medicalLocator.POJOs;

import java.util.ArrayList;

public class child extends Person_File{
    private String name;
    private String surname;
    private boolean gender;
    private String dob;
    private String relationship;
    private ArrayList<MedicalVisits> visits;

    public child(String name, String surname, boolean gender, String dob,String rel, ArrayList<MedicalVisits> visits) {
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.dob = dob;
        this.relationship = rel;
        this.visits = visits;
    }

    public child() {
    }

    @Override
    public String toString() {
        return
                "Name='" + name + '\'' +
                "\nSurname='" + surname + '\'' +
                "\nGender=" + gender +
                "\nDate of birth='" + dob + '\'' +
                        "\nRelationship='" + relationship+ '\'' +
                "\nVisits=" + visits;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean isGender() {
        return gender;
    }

    @Override
    public void setGender(boolean gender) {
        this.gender = gender;
    }

    @Override
    public String getDob() {
        return dob;
    }

    @Override
    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public ArrayList<MedicalVisits> getVisits() {
        return visits;
    }

    @Override
    public void setVisits(ArrayList<MedicalVisits> visits) {
        this.visits = visits;
    }
}
