package com.home.ui.tables.models;

/**
 * Created by Narendra Singh on 28/9/17.
 */

public class UserModel {
    String name;
    String email;
    String password;
    String mobile_no;
    String img_url;
    String userId;

    public UserModel() {

    }

    public UserModel(String name, String email, String pass, String mobile_no, String img_url, String user_id) {
        this.name = name;
        this.email = email;
        this.password = pass;
        this.mobile_no = mobile_no;
        this.img_url = img_url;
        this.userId = user_id;
    }

    public String getUserId() {
        return userId == null ? "" : userId.equals("null") ? "" : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name == null ? "" : name.equals("null") ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email == null ? "" : email.equals("null") ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password == null ? "" : password.equals("null") ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile_no() {
        return mobile_no == null ? "" : mobile_no.equals("null") ? "" : mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getImg_url() {
        return img_url == null ? "" : img_url.equals("null") ? "" : img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
