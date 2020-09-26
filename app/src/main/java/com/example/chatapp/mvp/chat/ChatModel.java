package com.example.chatapp.mvp.chat;

import com.example.chatapp.common.AwesomeMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChatModel {

    /*
    переменные для DB  с узлом messages
     */

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference messagesDatabaseReference;
    private ChildEventListener messagesChildEventListener;

    /*
    переменные для работы c DB узлом  users
     */
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;

    // для работы со storage
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    interface initCallBack{
        void init();
    }

    public void initDB(initCallBack init){
        auth = FirebaseAuth.getInstance();

        // получаем доступ к корневой папке БД
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        // ссылка на узел БД
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

        // ссылаемся на папку которая создана в storage firebase
        storageReference = firebaseStorage.getReference().child("chat_images");
    }

    public String getCurrentUserId(initCallBack init){
        return auth.getCurrentUser().getUid();
    }

    public void pushDBFireBase(initCallBack init , AwesomeMessage message){
        messagesDatabaseReference.push().setValue(message);
    }
}
