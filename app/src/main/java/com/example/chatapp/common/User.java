package com.example.chatapp.common;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {

    private String name = "";
    private String email = "";
    private String id= "";
    private String textUserInfo = "";
    private int avatarMockUpResource = 0;
    private String avatarUri = "";

    public User() {

    }

    public User(String name, String email, String id , String avatarUri , String textUserInfo) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.avatarUri=avatarUri;
        this.textUserInfo=textUserInfo;
    }


    public String getTextUserInfo() {
        return textUserInfo;
    }

    public void setTextUserInfo(String textUserInfo) {
        this.textUserInfo = textUserInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarMockUpResource() {
        return avatarMockUpResource;
    }

    public void setAvatarMockUpResource(int avatarMockUpResource) {
        this.avatarMockUpResource = avatarMockUpResource;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }
}