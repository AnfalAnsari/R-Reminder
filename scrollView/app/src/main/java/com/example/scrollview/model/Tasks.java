package com.example.scrollview.model;

import java.sql.Time;
import java.util.Calendar;

public class Tasks {
    private String imageURL;
    private String title;
    private Calendar mCalendar;
    private Time time;
    private String venu;
    private String type;
    private int ID;

    public void setType(String type) {
        this.type = type;
    }

    public Tasks(String imageURL, String title, Calendar mCalendar, Time time, String venu, String type,int ID) {
        this.imageURL = imageURL;
        this.title = title;
        this.mCalendar = mCalendar;
       this.ID = ID ;
        this.venu = venu;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setVenu(String venu) {
        this.venu = venu;
    }

    public String getVenu() {
        return venu;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getmCalendar() {
        return mCalendar;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setmCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar ;
    }

    public int getID() {
        return ID;
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public Time getTime() {
        return time;
    }


    public Tasks() {
        imageURL="https://img.channeli.in/static/images/imglogo.png";
        title= "I love India";
        mCalendar = Calendar.getInstance();
        time= new Time(0);

    }




}
