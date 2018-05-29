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
    public String getAddress()
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

    public void setFirstName(String fName) { this.mFirstName = fName;}
    public void setLasttName(String lName) { this.mLastName = lName;}
    public void setAddress(String address) { this.mEmail = address;}
    public void setPhoneNumber(Date mBirthday) { this.mBirthday = mBirthday;}
    public void setUserName (String userName) {this.mUserName = userName;}
}
