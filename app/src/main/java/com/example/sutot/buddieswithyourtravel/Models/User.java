package com.example.sutot.buddieswithyourtravel.Models;

import java.util.Date;
import java.util.List;

public class User {

    //a felhasznalo osztaly mezoi
    private String mUserName;
    private String mName;
    private String mEmail;
    private String mBio;
    private String mProfilePic;
    private Date mBirthday;
    private int mCreatedTripsNr;

    //konstruktor
    public User(String username, String name, String email, Date mBirthday, String bio, String profPic) {
        this.mUserName = username;
        this.mName = name;
        this.mEmail = email;
        this.mBio = bio;
        this.mProfilePic = profPic;
        this.mBirthday = mBirthday;
        this.mCreatedTripsNr = 0;
    }

    //copy konstruktor
    public User(User pm) {
        this.mUserName = pm.getUserName();
        this.mName = pm.getName();
        this.mEmail = pm.getEmail();
        this.mBio = pm.getBio();
        this.mProfilePic = pm.getProfilePicture();
        this.mBirthday = pm.getBirthday();
        this.mCreatedTripsNr = pm.getCreatedTripsNr();
    }

    public User() {

    }

    //getterek
    public String getUserName() {
        return mUserName;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getBio() {
        return mBio;
    }

    public String getProfilePicture() {
        return mProfilePic;
    }

    public Date getBirthday() {
        return mBirthday;
    }

    public int getCreatedTripsNr() {
        return mCreatedTripsNr;
    }

    //setterek
    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public void setName(String Name) {
        this.mName = Name;
    }

    public void setEmail(String address) {
        this.mEmail = address;
    }

    public void setBio(String bio) {
        this.mBio = bio;
    }

    public void setProfilePicture(String pmProfile) {
        this.mProfilePic = pmProfile;
    }

    public void setBirthday(Date birthday) {
        this.mBirthday = birthday;
    }

    public void setCreatedTripsNr (int nr) { this.mCreatedTripsNr = nr;}

    public void addNewTrip() {
        this.mCreatedTripsNr++;
    }

    public void removeTrip() {
        if (this.getCreatedTripsNr() > 0) {
            this.mCreatedTripsNr--;
        }
    }
}
