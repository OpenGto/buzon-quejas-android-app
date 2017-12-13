package com.opengto.buzondequejas.model;

/**
 * Created by joel_barron on 08/12/17.
 */

public class Post {

    private String description; //*
    private String postType;    //*
    private String postStatus;  //*
    private User user;          //* (en el card solo se muestra el displayname)
    private String tags;        //*

    // cardview
    private String id;
    private String createdAt;
    private String pictureUrl;
    private String title;
    private int likes;
    private int dislikes;


    public Post() {
    }

    // constructor ful
    public Post(String id, String pictureUrl, String title, String description, String createdAt, String postType, String postStatus, User user, String tags, int likes, int dislikes) {
        this.id = id;
        this.pictureUrl = pictureUrl;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.postType = postType;
        this.postStatus = postStatus;
        this.user = user;
        this.tags = tags;
        this.likes = likes;
        this.dislikes = dislikes;
    }


    //constructor cardview

    public Post(User user, String id, String createdAt, String pictureUrl, String title, int likes, int dislikes) {
        this.user = user;
        this.id = id;
        this.createdAt = createdAt;
        this.pictureUrl = pictureUrl;
        this.title = title;
        this.likes = likes;
        this.dislikes = dislikes;
    }


    // metodos get

    public String getId() {
        return id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getPostType() {
        return postType;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public User getUser() {
        return user;
    }

    public String getTags() {
        return tags;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getDescription() {
        return description;
    }

    // metodos set


    public void setId(String id) {
        this.id = id;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
