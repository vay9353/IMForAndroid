package com.parta.mychatapp.DataBaseBean;

import org.litepal.crud.DataSupport;

/**
 * Created by Vay on 2018/3/30.
 */

public class UserFriends extends DataSupport {

    private String press;
    private String userId;
    private String friends;

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }
}
