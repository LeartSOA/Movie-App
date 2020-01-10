package com.Model;

import java.io.Serializable;

public class MovieWatchlist implements Serializable {
    private int id;
    private String title;
    private String year;
    private String runtime;
    private double rating;
    private String imgLink;

    public MovieWatchlist() {
    }

    public MovieWatchlist(int id, String title, String year, String runtime, double rating, String imgLink) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.runtime = runtime;
        this.rating = rating;
        this.imgLink = imgLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
}
