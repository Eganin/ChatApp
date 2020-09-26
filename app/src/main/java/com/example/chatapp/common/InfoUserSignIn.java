package com.example.chatapp.common;

public class InfoUserSignIn {

    private String nickName;
    private String email;
    private String password;
    private String repeatPassword;

    public InfoUserSignIn(){

    }

    public InfoUserSignIn(String nickName , String email , String password , String repeatPassword){
        this.nickName=nickName;
        this.email=email;
        this.password=password;
        this.repeatPassword=repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
