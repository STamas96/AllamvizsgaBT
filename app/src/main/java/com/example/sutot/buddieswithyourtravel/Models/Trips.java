package com.example.sutot.buddieswithyourtravel.Models;

import android.net.Uri;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Trips {

    //kirandulas osztaly mezoi
    private String mID;
    private String mOwnerID;
    private String mTitle;
    private String mFilePath;
    private String mShortDescription;
    private Date mStartDate;
    private Date mEndDate;
    private Date mTripCreated;
    private Date mLastTimeModified;
    private String mDetailedDescription;

    //szukseges: ures konstruktor
    public Trips(){

    }

    //konstruktor
    public Trips(String ID, String owner, String title, String Images, String shortdesc, Date startdate, Date enddate,Date
                 created,Date modified,String longDesc){
        this.mID=ID;
        this.mOwnerID=owner;
        this.mTitle = title;
        this.mFilePath = Images;
        this.mShortDescription=shortdesc;
        this.mStartDate=startdate;
        this.mEndDate=enddate;
        this.mTripCreated=created;
        this.mLastTimeModified=modified;
        this.mDetailedDescription=longDesc;
    }

    public Trips(Trips temp)
    {
        this.mID = temp.mID;
        this.mOwnerID = temp.mOwnerID;
        this.mTitle = temp.mTitle;
        this.mFilePath = temp.mFilePath;
        this.mShortDescription = temp.mShortDescription;
        this.mStartDate = temp.mStartDate;
        this.mEndDate = temp.mEndDate;
        this.mTripCreated = temp.mTripCreated;
        this.mLastTimeModified = temp.mLastTimeModified;
        this.mDetailedDescription = temp.mDetailedDescription;
    }

    //getters
    public String getID() {
        return this.mID;
    }
    public String getOwnerID() {
        return this.mOwnerID;
    }
    public String getTitle() {
        return this.mTitle;
    }
    public String getFilePath() {
        return this.mFilePath;
    }
    public String getShortDescription() {
        return this.mShortDescription;
    }
    public Date getStartDate() {
        return this.mStartDate;
    }
    public Date getEndDate() {
        return this.mEndDate;
    }
    public Date getmTripCreated() {return this.mTripCreated;}
    public String getDetailedDescription() {return this.mDetailedDescription;}
    public Date getmLastTimeModified() {return this.mLastTimeModified;}


    //setters
    public void setOwnerID(String owner) {this.mOwnerID = owner;}
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public void setFilePath(String fpath){this.mFilePath=fpath;}
    public void setShortDescription(String mShortDescription) {this.mShortDescription = mShortDescription;}
    public void setStartDate(Date mStartDate) {
        this.mStartDate = mStartDate;
    }
    public void setEndDate(Date mEndDate) {
        this.mEndDate = mEndDate;
    }
    public void setTripCreated(Date created) {this.mTripCreated = created;}
    public void setDetailedDescription(String longDesc){ this.mDetailedDescription = longDesc;}
    public void setLastTimeModified (Date lmodified) {this.mLastTimeModified = lmodified;}



}
