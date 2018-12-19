package com.parta.mychatapp.DataBaseBean;

import org.litepal.crud.DataSupport;

/**
 * Created by Vay on 2018/3/23.
 */

public class User extends DataSupport {
    private String press;
    private String userId;
    private String nickName;
    private String photoName;
    private String backPhotoName;
    private String personaltTalk;
    private String loveThing;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getBackPhotoName() {
        return backPhotoName;
    }

    public void setBackPhotoName(String backPhotoName) {
        this.backPhotoName = backPhotoName;
    }

    public String getPersonaltTalk() {
        return personaltTalk;
    }

    public void setPersonaltTalk(String personaltTalk) {
        this.personaltTalk = personaltTalk;
    }

    public String getLoveThing() {
        return loveThing;
    }

    public void setLoveThing(String loveThing) {
        this.loveThing = loveThing;
    }
}
