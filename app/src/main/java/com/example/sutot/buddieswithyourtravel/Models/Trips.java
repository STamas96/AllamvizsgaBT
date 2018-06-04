package com.example.sutot.buddieswithyourtravel.Models;

import android.net.Uri;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

//kirandulas osztaly
public class Trips {

    private String mID;
    private String mOwnerID;
    private String mTitle;
    private String mFilePath;
    private String mShortDescription;
    private Date mStartDate;
    private Date mEndDate;
    private List<String> mInterestedID;
    private Date mTripCreated;
    private Date mLastTimeModified;

    public Trips(){

    }

    public Trips(String ID, String owner, String title, String Images, String shortdesc, Date startdate, Date enddate,Date
                 created,Date modified){
        this.mID=ID;
        this.mOwnerID=owner;
        this.mTitle = title;
        this.mFilePath = Images;
        this.mShortDescription=shortdesc;
        this.mStartDate=startdate;
        this.mEndDate=enddate;
        this.mTripCreated=created;
        this.mLastTimeModified=modified;
    }

    //getters
    public String getID() {
        return this.mID;
    }
    public Date getEndDate() {
        return this.mEndDate;
    }
    public Date getStartDate() {
        return this.mStartDate;
    }
    public String getTitle() {
        return this.mTitle;
    }
    public String getOwnerID() {
        return this.mOwnerID;
    }
    public String getShortDescription() {
        return this.mShortDescription;
    }
    public List<String> getInteresteds() {
        return this.mInterestedID;
    }
    public String getFilePath() {
        return this.mFilePath;
    }
    public Date getmTripCreated() {return this.mTripCreated;}
    public Date getmLastTimeModified() {return this.mLastTimeModified;}


    //setters
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public void setEndDate(Date mEndDate) {
        this.mEndDate = mEndDate;
    }
    public void setStartDate(Date mStartDate) {
        this.mStartDate = mStartDate;
    }
    public void setOwnerID(String owner) {this.mOwnerID = owner;}
    public void setTripCreated(Date created) {this.mTripCreated = created;}
    public void setLastTimeModified (Date lmodified) {this.mLastTimeModified = lmodified;}
    public void setFilePath(String fpath){this.mFilePath=fpath;}
    public void setShortDescription(String mShortDescription) {this.mShortDescription = mShortDescription;}

    //ha valaki mar nem erdeklodik
    public void removeInterested(String userID)
    {
        Iterator<String> iter = getInteresteds().iterator();
        while (iter.hasNext()) {
            String o = iter.next();
            if ( o.equals(userID)){
            iter.remove();
            }
        }
    }

    //ha valaki erdeklodik
    public void addInterested(String userID)
    {
        if (!userID.isEmpty()){
            mInterestedID.add(userID);
        }
    }
}
