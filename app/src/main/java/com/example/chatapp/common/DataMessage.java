package com.example.chatapp.common;

public class DataMessage {
    String textMessage;
    String userName;
    String recipientId;

    public DataMessage(){

    }

    public DataMessage(String textMessage,String userName,String recipientId){
        this.textMessage=textMessage;
        this.userName=userName;
        this.recipientId=recipientId;
    }


    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString(){
        String stringResult = "Text: "+ getTextMessage()+" UserName:" +
                getUserName() + " RecipientID" + getRecipientId();

        return stringResult;
    }
}
