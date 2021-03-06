package com.example.chatapp.mvp.chat;

import android.content.Intent;
import android.net.Uri;

import com.example.chatapp.common.AwesomeMessage;
import com.example.chatapp.common.DataMessage;
import com.example.chatapp.mvp.menu.customize.CustomizeView;

import static com.example.chatapp.contacts.ContactException.Text.TEXT_INTENT_IMAGE;
import static com.example.chatapp.contacts.ContactException.Types.MIME_TYPE_IMAGES;
import static com.example.chatapp.contacts.ContactException.Types.RC_IMAGE_PICKER;

public class ChatPresenter {

    private ChatView view;
    private CustomizeView customizeView;
    private  ChatModel model;

    public ChatPresenter(ChatModel model){
        this.model=model;
    }


    public void attachView(ChatView view){
        this.view=view;
    }


    public void detachView() {
        this.view = null;
    }

    public void initDB(ChatView chatView){
        model.initDB();
        attachView(chatView);
    }

    public void sendMessage(DataMessage dataMessage, ChatView chatView){
        AwesomeMessage message = new AwesomeMessage();
        message.setText(dataMessage.getTextMessage());
        message.setName(dataMessage.getUserName());
        message.setImageUrl(null);
        message.setIsImage(false);
        message.setSender(model.getCurrentUserId());
        message.setRecipient(dataMessage.getRecipientId());

        model.pushDBFireBase(message);

        attachView(chatView);
        view.clearEditText();
    }

    public void newIntentForImageChoose(){
        // intent для получения контента-изображения
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME_TYPE_IMAGES);// mime type
        // получаем изображения с локального хранилища
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        view.startActivityForResult(Intent.createChooser(intent,TEXT_INTENT_IMAGE),
                RC_IMAGE_PICKER);
    }

    public void downloadFile(Intent data){
        // получаем Uri изображения
        Uri selectedImageUri = data.getData();
        // получаем последний сегмент изображения
        model.getStorageReferenceFromImage(new ChatModel.initCallBack() {
            @Override
            public void init() {
                view.clearEditText();
            }
        }, selectedImageUri,
                view.getDataFromMessage());
    }


    public void listenerMessages(final ChatView chatView) {
        DataMessage dataMessage = view.getDataFromMessage();
        String recipientId = dataMessage.getRecipientId();
        model.messagesChildEventListener(new ChatModel.interactionWithAdapter() {
            @Override
            public void interaction(AwesomeMessage message) {
                attachView(chatView);
                view.interactionWithAdapter(message);
            }
        },recipientId);
    }

    public void listenerUsers() {
        model.usersChildEventListener(new ChatModel.withUserName() {
            @Override
            public void setUserName(String name) {
                view.userName=name;
            }
        });
    }

    public void signOut(ChatView chatView){
        model.signOutDB();
        attachView(chatView);
    }
}
