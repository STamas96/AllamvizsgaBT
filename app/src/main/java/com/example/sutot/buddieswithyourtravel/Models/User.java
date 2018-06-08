package com.example.sutot.buddieswithyourtravel.Models;

import java.util.Date;
import java.util.List;

public class User {

    //a felhasznalo osztaly mezoi
    private String mUserName;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mBio;
    private String mProfilePic;
    private Date mBirthday;

    //konstruktor
    public User(String username,String fName, String lname, String email, Date mBirthday,String bio,String profPic){
        this.mUserName = username;
        this.mFirstName = fName;
        this.mLastName = lname;
        this.mEmail = email;
        this.mBio = bio;
        this.mProfilePic = profPic;
        this.mBirthday = mBirthday;
    }

    //copy konstruktor
    public User(User pm)
    {
        this.mUserName = pm.getUserName();
        this.mFirstName = pm.getFirstName();
        this.mLastName = pm.getLastName();
        this.mEmail = pm.getEmail();
        this.mBio = pm.getBio();
        this.mProfilePic = pm.getProfilePicture();
        this.mBirthday = pm.getBirthday();
    }

    public User()
    {

    }

    //getterek
    public String getUserName() { return mUserName; }
    public String getFirstName(){
        return mFirstName;
    }
    public String getLastName(){
        return mLastName;
    }
    public String getEmail()
    {
        return mEmail;
    }
    public String getBio() {return mBio;}
    public String getProfilePicture() {return mProfilePic;}
    public Date getBirthday()
    {
        return mBirthday;
    }

    //setterek
    public void setUserName (String userName) {this.mUserName = userName;}
    public void setFirstName(String fName) { this.mFirstName = fName;}
    public void setLastName(String lName) { this.mLastName = lName;}
    public void setEmail(String address) { this.mEmail = address;}
    public void setBio(String bio){this.mBio = bio;}
    public void setProfilePicture(String pmProfile) {this.mProfilePic = pmProfile;}
    public void setBirthday (Date birthday) {this.mBirthday = birthday;}
}
