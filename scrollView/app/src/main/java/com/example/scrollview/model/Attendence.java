package com.example.scrollview.model;

import java.util.ArrayList;

public class Attendence {
    private String code;
    private String name;
    private String profName;
    private String number;
    private String emailID;
    private String profileURL;
    private String status;
    private double present;
    private double total;
    private double percentage;
    private int ID;
    private ArrayList<Boolean> prevRecord= new ArrayList<>();


    public ArrayList<Boolean> getPrevRecord() {
        return prevRecord;
    }

    public ArrayList<Boolean> undoPrevRecord() {
        prevRecord.remove(prevRecord.size()-1);
        return prevRecord;
    }
    public ArrayList<Boolean> resetPrevRecord() {
        prevRecord.clear();
        return prevRecord;
    }

    public void setPrevRecord(ArrayList<Boolean> prevRecord,boolean prev) {
        prevRecord.add(prev);
        this.prevRecord = prevRecord;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }



    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getProfName() {
        return profName;
    }

    public String getNumber() {
        return number;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public double getPresent() {
        return present;
    }

    public double getTotal() {
        return total;
    }

    public double getPercentage() {
        return Math.floor((present / total )*1000)/10;
    }

    public String getstatus(){return status;}

    public Attendence()
    {
        code = "MIN 106";
        name="Engineering Thermodynamics";
        profName = "Dhananshri M joglekar";
        present =0;
        total =0;
        percentage =0;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public void setPresent(double present) {
        this.present = present;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Attendence(String code, String name, String profName, String number, String emailID, String profileURL, double present, double total, double percentage,String status) {
        this.code = code;
        this.name = name;
        this.profName = profName;
        this.number = number;
        this.emailID = emailID;
        this.profileURL = profileURL;
        this.present = present;
        this.total = total;
        this.percentage = percentage;
        this.status = status;
    }
}
