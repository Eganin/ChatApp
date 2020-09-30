package com.example.chatapp.common;

public class NewUserData {

    public int avatar;
    public String customizeEmail;
    public String customizePassword;
    public String textUserInfo;
    public String nickName;

    public NewUserData() {
    }

    public NewUserData(Object avatar, String email, String password, String textUserInfo,
                       String nickName) {
        this.customizeEmail = email;
        this.customizePassword = password;
        this.textUserInfo = textUserInfo;
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getCustomizeEmail() {
        return customizeEmail;
    }

    public void setCustomizeEmail(String customizeEmail) {
        this.customizeEmail = customizeEmail;
    }

    public String getCustomizePassword() {
        return customizePassword;
    }

    public void setCustomizePassword(String customizePassword) {
        this.customizePassword = customizePassword;
    }

    public String getTextUserInfo() {
        return textUserInfo;
    }

    public void setTextUserInfo(String textUserInfo) {
        this.textUserInfo = textUserInfo;
    }
}
