package com.example.scrollview.model;

public class Subject {
    private String code;
    private String name;
    private  String profName;
    private String number;
    private String emailID;
    private String profileURL;
    private String attendence;
    private int ID;

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

    public String getAttendanceURL() {
        return attendence ;
    }

    public Subject()
    {
        code = "MIN 106";
        name="Engineering Thermodynamics";
        profName = "Dhananshri M joglekar";
        attendence="20/90";
    }



    public Subject(String code, String name, String profName, String number, String emailID, String profileURL,String attendence) {
        this.code = code;
        this.name = name;
        this.profName = profName;
        this.number = number;
        this.emailID = emailID;
        this.profileURL = profileURL;
        this.attendence = attendence;
    }

}
