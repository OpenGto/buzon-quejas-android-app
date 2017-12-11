package com.opengto.buzondequejas.model;

/**
 * Created by joel_barron on 08/12/17.
 */

public class Post {

    private String picture;
    private String title;
    private String username;
    private String time;
    private String type;
    private String status;
    private int likes;
    private int dislikes;


    public Post(String picture, String title, String username, String time, String type, String status, int likes, int dislikes) {
        this.picture = picture;
        this.title = title;
        this.username = username;
        this.time = time;
        this.type = type;
        this.status = status;
        this.likes = likes;
        this.dislikes = dislikes;
    }


    // metodos get

    public String getPicture() {
        return picture;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }


    // metodos set


    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
