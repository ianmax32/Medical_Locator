package com.naitech.medicalLocator.POJOs;

public class feeling {
    String date;
    String feelings;

    public feeling(String date, String feelings) {
        this.date = date;
        this.feelings = feelings;
    }

    public feeling() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeelings() {
        return feelings;
    }

    public void setFeelings(String feelings) {
        this.feelings = feelings;
    }


}
