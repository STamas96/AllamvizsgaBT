package com.example.sutot.buddieswithyourtravel.Models;

import java.util.Date;

public class Comment {

    private String mUID;
    private String mType;
    private String mUserName;
    private String mComment;
    private Date mPostedDate;
    private String mFilePath;

    public Comment() {

    }

    public Comment(String muid, String musername, String mcomm, Date post,String filepath) {
        this.mUID = muid;
        this.mComment = mcomm;
        this.mUserName = musername;
        this.mPostedDate = post;
        this. mFilePath = filepath;
    }

    public Comment(Comment comm)
    {
        this.mUID = comm.mUID;
        this.mComment = comm.mComment;
        this.mUserName = comm.mUserName;
        this.mPostedDate = comm.mPostedDate;
        this.mFilePath = comm.mFilePath;
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


    public String getFilePath() {return this.mFilePath;}

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

    public void setFilePath(String path)
    {
        this.mFilePath = path;
    }

    public void setUserName(String usern) {
        this.mUserName = usern;
    }
}
