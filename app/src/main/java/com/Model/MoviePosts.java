package com.Model;

import java.io.Serializable;

public class MoviePosts implements Serializable {
    private String id;
    private String name;
    private String desc;
    private String imglink;
    private String numOfLikes;
    private String numOfComments;
    private String date;
    private String movie_title;
    private String post_id;

    public MoviePosts() {
    }

    public MoviePosts(String id, String name, String desc, String imglink, String numOfLikes, String numOfComments, String date, String movie_title, String post_id) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.imglink = imglink;
        this.numOfLikes = numOfLikes;
        this.numOfComments = numOfComments;
        this.date = date;
        this.movie_title = movie_title;
        this.post_id = post_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(String numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public String getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(String numOfComments) {
        this.numOfComments = numOfComments;
    }
}
