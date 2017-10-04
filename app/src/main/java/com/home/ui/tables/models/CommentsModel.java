package com.home.ui.tables.models;

import java.util.HashMap;

/**
 * Created by Narendra Singh on 2/10/17.
 */

public class CommentsModel {
    long post_id;
    String comment;
    String user_name;
    String user_img;
    long time_stamp;

    public CommentsModel() {

    }

    public CommentsModel(HashMap hashMap) {
        this.post_id = (long) hashMap.get("post_id");
        this.comment = (String) hashMap.get("comment");
        this.user_name = (String) hashMap.get("user_name");
        this.user_img = (String) hashMap.get("user_img");
        this.time_stamp = (long) hashMap.get("time_stamp");
    }

    public long getPost_id() {
        return post_id;
    }

    public void setPost_id(long post_id) {
        this.post_id = post_id;
    }

    public String getComment() {
        return comment == null ? "" : comment.equals("null") ? "" : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_name() {
        return user_name == null ? "" : user_name.equals("null") ? "" : user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_img() {
        return user_img == null ? "" : user_img.equals("null") ? "" : user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
