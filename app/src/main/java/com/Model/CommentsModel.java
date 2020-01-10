package com.Model;

import java.io.Serializable;

public class CommentsModel implements Serializable {
    private String name;
    private String description;
    private String uid;
    private String date;
    private String imgLink;

    public CommentsModel() {
    }

    public CommentsModel(String name, String description, String uid, String date, String imgLink) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.date = date;
        this.imgLink = imgLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
