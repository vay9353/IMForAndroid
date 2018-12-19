package com.parta.mychatapp.POJO;

/**
 * Created by Vay on 2018/3/18.
 */

public class ChatUser {
    public String username;
    public int imageId;

    public ChatUser(String username, int imageId) {
        this.username = username;
        this.imageId = imageId;
    }

    public String getUsername() {
        return username;
    }

    public int getImageId() {
        return imageId;
    }
}
