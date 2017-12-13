package com.opengto.buzondequejas.model;

/**
 * Created by joel_barron on 09/12/17.
 */

public class User {
    private String id;
    private String displayName;
    private String email;
    private String password;
    private String urlAvatar;


    public User() {
    }

    public User(String id, String displayName, String email, String password, String urlAvatar) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.urlAvatar = urlAvatar;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String id, String displayName, String urlAvatar) {
        this.id = id;
        this.displayName = displayName;
        this.urlAvatar = urlAvatar;
    }


    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }
}
