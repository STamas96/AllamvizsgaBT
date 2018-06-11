package com.example.sutot.buddieswithyourtravel.Models;

import java.util.Date;

public class Comment {

    private String mUID;
    private String mUserName;
    private String mComment;
    private Date mPostedDate;

    public Comment() {

    }

    public Comment(String muid, String musername, String mcomm, Date post) {
        this.mUID = muid;
        this.mComment = mcomm;
        this.mUserName = musername;
        this.mPostedDate = post;
    }

    public String getUID() {
        return this.mUID;
    }

    public String getUserName() {
        return this.mUserName;
    }

    public String getComment() {
        return this.mComment;
    }

    public Date getPostedDate() {
        return this.mPostedDate;
    }

    public void setUID(String uid) {
        this.mUID = uid;
    }

    public void setPostedDate(Date post) {
        this.mPostedDate = post;
    }

    public void setComment(String comm) {
        this.mComment = comm;
    }

    public void setUserName(String usern) {
        this.mUserName = usern;
    }
}
