package com.example.chatapp;

public class NoInfoFromEditTextException extends Exception {

    private String messageException;

    public NoInfoFromEditTextException(String messageException) {
        this.messageException = messageException;
    }

    @Override
    public String toString() {
        return messageException;
    }

    public String getMessageException() {
        return messageException;
    }

    public void setMessageException(String messageException){
        this.messageException=messageException;
    }
}
