package com.example.sutot.buddieswithyourtravel.Models;

import java.util.Date;
import java.util.List;

public class User {

    //memberlist
    private String mUserName;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPhoneNumber;
    private String mProfilePic;
    private List<String> mLanguages;
    private List<String> mFriends;
    private Date mBirthday;

    //constructor
    public User(String username,String fName, String lname, String email, Date mBirthday){
        this.mUserName = username;
        this.mFirstName = fName;
        this.mLastName = lname;
        this.mEmail = email;
        this.mBirthday = mBirthday;
        this.mProfilePic = null;
        this.mPhoneNumber = null;
        this.mLanguages = null;
        this.mFriends = null;
    }

    public User(User pm)
    {
        this.mUserName = pm.getUserName();
        this.mFirstName = pm.getFirstName();
        this.mLastName = pm.getLastName();
        this.mEmail = pm.getEmail();
        this.mProfilePic = pm.getProfilePicture();
        this.mBirthday = pm.getBirthday();
        this.mPhoneNumber = null;
        this.mLanguages = null;
        this.mFriends = null;
    }

    public User()
    {

    }

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
    public Date getBirthday()
    {
        return mBirthday;
    }
    public List<String> getLanguages() {return mLanguages;}
    public List<String> getFriends() { return mFriends; }
    public String getPhoneNumber() { return mPhoneNumber; }
    public String getProfilePicture() {return mProfilePic;}

    public void setFirstName(String fName) { this.mFirstName = fName;}
    public void setLastName(String lName) { this.mLastName = lName;}
    public void setEmail(String address) { this.mEmail = address;}
    public void setPhoneNumber(Date mBirthday) { this.mBirthday = mBirthday;}
    public void setUserName (String userName) {this.mUserName = userName;}
    public void setBirthday (Date birthday) {this.mBirthday = birthday;}
    public void setProfilePicture(String pmProfile) {this.mProfilePic = pmProfile;}
}
