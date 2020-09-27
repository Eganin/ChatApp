package com.example.chatapp.mvp.chat;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatapp.common.AwesomeMessage;
import com.example.chatapp.common.DataMessage;
import com.example.chatapp.common.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.chatapp.contacts.ContactException.Text.CLEAR_EDIT_TEXT;

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

    interface interactionWithAdapter{
        void interaction(AwesomeMessage message);
    }

    interface withUserName{
        void setUserName(String name);
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

    public void getStorageReferenceFromImage(initCallBack init,Uri uriImage, DataMessage dataMessage){
        StorageReference imageReference = storageReference
                .child(uriImage.getLastPathSegment());
        uploadFile(imageReference, uriImage,dataMessage);

        if(init != null){
            init.init();
        }
    }

    private void uploadFile(final StorageReference imageReference , Uri selectedImageUri,
                            final DataMessage dataMessage){
        // загружаем локальный файл-изображение в firebase storage

        // загружаем файл
        UploadTask uploadTask = imageReference.putFile(selectedImageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,
                Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    // если не получилось занрузить файл
                    throw task.getException();
                }

                return imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // получаем ссылку изображения котрое потом скачаем с помощью Glide
                    Uri downloadUri = task.getResult();
                    AwesomeMessage awesomeMessage = new AwesomeMessage();
                    awesomeMessage.setImageUrl(downloadUri.toString());
                    awesomeMessage.setName(dataMessage.getUserName());
                    awesomeMessage.setText(dataMessage.getTextMessage());
                    awesomeMessage.setSender(getCurrentUserId(new initCallBack() {
                        @Override
                        public void init() {

                        }
                    }));
                    awesomeMessage.setRecipient(dataMessage.getRecipientId());
                    // отправляем в DB
                    messagesDatabaseReference.push().setValue(awesomeMessage);
                } else {

                }
            }
        });
    }

    public void messagesChildEventListener(final interactionWithAdapter interactionWithAdapter ,
                                           final String recipientUserId){
        // обработчик всех изменений в базе данных
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // срабатывает при добавлении элемента и при загрузке всех элементов

                // получаем данные и помещаем их в класс
                AwesomeMessage message = snapshot.getValue(AwesomeMessage.class);

                if(message.getSender().equals(auth.getCurrentUser().getUid())
                        && message.getRecipient().equals(recipientUserId)){
                    // проверка наше ли сообщение для данного пользователя
                    message.setMineMessage(true);// мое сообщение
                    if(interactionWithAdapter != null){
                        interactionWithAdapter.interaction(message);
                    }
                }else if(message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId)){
                    // проверка сообщения от отправителя
                    message.setMineMessage(false);// сообщение собеседника
                    if(interactionWithAdapter != null){
                        interactionWithAdapter.interaction(message);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
                // срабатывает при  изменении элемента

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // срабатывает при  удалении элемента

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // срабатываеи при перемещении элемента


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ошибка размещения элемента

            }
        };

        // добавляем к базе listener
        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);
    }

    public void usersChildEventListener(final withUserName withUserName){
        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // получаем данные и помещаем их в класс
                User user = snapshot.getValue(User.class);

                String userID = user.getId();
                String userFirebaseID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // сравниваем тот же ли id находится в класса user и firebase
                if(userID.equals(userFirebaseID)){
                    if(withUserName != null){
                        withUserName.setUserName(user.getName());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        usersDatabaseReference.addChildEventListener(usersChildEventListener);
    }
}
