package com.example.chatapp.mvp.chat;

import com.example.chatapp.common.AwesomeMessage;
import com.example.chatapp.common.DataMessage;

public class ChatPresenter {

    private ChatView view;
    private final ChatModel model;

    public ChatPresenter(ChatModel model){
        this.model=model;
    }

    public void attachView(ChatView view){
        this.view=view;
    }

    public void detachView() {
        this.view = null;
    }

    public void initDB(){
        model.initDB(new ChatModel.initCallBack() {
            @Override
            public void init() {

            }
        });
    }

    public void sendMessage(DataMessage dataMessage){
        AwesomeMessage message = new AwesomeMessage();
        message.setText(dataMessage.getTextMessage());
        message.setName(dataMessage.getUserName());
        message.setImageUrl(null);
        message.setSender(model.getCurrentUserId(new ChatModel.initCallBack() {
            @Override
            public void init() {

            }
        }));
        message.setRecipient(dataMessage.getRecipientId());

        model.pushDBFireBase(new ChatModel.initCallBack() {
            @Override
            public void init() {

            }
        },message);
    }


}
