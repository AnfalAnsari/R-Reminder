package com.example.scrollview.model;

public class Schedule {
    private String code;
    private String name;
    private  String profName;
    private String venue;
    private String time;
    private String type;

    public String getType() {
        return type;
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

    public String getVenue() {
        return venue;
    }

    public String getTime() {
        return time;
    }
    private int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Schedule() {
        code = "MIN 106";
        name="Engineering Thermodynamics";
        profName = "Dhananshri M joglekar";
        venue = "LHC-205";
        time = "1:00-2:00";
        type = "Lecture";
    }

    public Schedule(String code, String name, String profName, String venue, String time,String type) {
        this.code = code;
        this.name = name;
        this.profName = profName;
        this.venue = venue;
        this.time = time;
        this.type = type;
    }
}
