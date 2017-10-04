package com.home.ui.tables.models;

import java.util.HashMap;

/**
 * Created by Narendra Singh on 29/9/17.
 */
public class PostModel {

    /**
     * types of the posted by user.
     */
    public static final int POST_TYPE_TEXT = 1;
    public static final int POST_TYPE_IMG = 2;


    long timestamp = 0;
    long like = 0;
    long comments = 0;
    String user_img;
    String user_name;
    long post_type;
    String post_text;
    String post_image;
    boolean isLiked;

    public PostModel() {

    }

    public PostModel(HashMap hashMap) {
        if (hashMap == null) return;
        this.like = (long) hashMap.get("like");
        this.timestamp = (long) hashMap.get("timestamp");
        this.comments = (long) hashMap.get("comments");
        this.user_img = (String) hashMap.get("user_img");
        this.user_name = (String) hashMap.get("user_name");
        this.post_text = (String) hashMap.get("post_text");
        this.post_image = (String) hashMap.get("post_image");
        this.post_type = (long) hashMap.get("post_type");
        this.isLiked = (boolean) hashMap.get("liked");

    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public String getUser_img() {
        return user_img == null ? "" : user_img.equals("null") ? "" : user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_name() {
        return user_name == null ? "" : user_name.equals("null") ? "" : user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public long getPost_type() {
        return post_type;
    }

    public void setPost_type(long post_type) {
        this.post_type = post_type;
    }

    public String getPost_text() {
        return post_text == null ? "" : post_text.equals("null") ? "" : post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public String getPost_image() {
        return post_image == null ? "" : post_image.equals("null") ? "" : post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }
}
