package com.example.scrollview.model;

public class User {

    private String name;
    private String email;
    private String batch;
    private String branch;
    private String year;
    private Boolean isAdmin;


    public User()
    {

    }


    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public User(String name, String email, String batch, String branch, String year) {
        this.name = name;
        this.email = email;
        this.batch = batch;
        this.branch = branch;
        this.year = year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBatch() {
        return batch;
    }

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }

}
