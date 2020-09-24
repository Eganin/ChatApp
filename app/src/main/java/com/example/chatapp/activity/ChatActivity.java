package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.chatapp.model.AwesomeMessage;
import com.example.chatapp.adapter.AwesomeMessageAdapter;
import com.example.chatapp.R;
import com.example.chatapp.model.User;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.chatapp.contacts.ContactException.IntentKeys.*;
import static com.example.chatapp.contacts.ContactException.Text.*;
import static com.example.chatapp.contacts.ContactException.Types.*;

public class ChatActivity extends AppCompatActivity {


    private ListView listView;
    private AwesomeMessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton imageButtonSendPhoto;
    private Button buttonSendMessage;
    private EditText editTextMessage;

    public static String userName;

    /*
    переменные для DB  с узлом messages
     */
    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    ChildEventListener messagesChildEventListener;

    /*
    переменные для работы c DB узлом  users
     */
    DatabaseReference usersDatabaseReference;
    ChildEventListener usersChildEventListener;

    // для работы со storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initFareBaseDB();
        getDataFromSignInActivity();
        findView();
        handlerEditText();
        handlerButtonSendMessage();
        handlerImageButtonSendPhoto();
        handlerMessagesChildEventListener();
        handlerUsersChildEventListener();
    }

    private void findView() {
        progressBar = findViewById(R.id.progressBar);
        imageButtonSendPhoto = findViewById(R.id.imageButtonSendPhoto);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        editTextMessage = findViewById(R.id.editTextMessage);
        listView = findViewById(R.id.listView);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        initListView();
    }

    private void initListView() {

        List<AwesomeMessage> awesomeMessages = new ArrayList<AwesomeMessage>();
        adapter = new AwesomeMessageAdapter(this, R.layout.message_item,
                awesomeMessages);

        listView.setAdapter(adapter);
    }


    private void handlerImageButtonSendPhoto() {
        imageButtonSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent для получения контента-изображения
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(MIME_TYPE_IMAGES);// mime type
                // получаем изображения с локального хранилища
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,TEXT_INTENT_IMAGE),
                        RC_IMAGE_PICKER);
            }
        });
    }

    private void handlerButtonSendMessage() {
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AwesomeMessage message = new AwesomeMessage();
                message.setText(editTextMessage.getText().toString());
                message.setName(userName);
                message.setImageUrl(null);

                // отправляем объект на Firebase DB
                messagesDatabaseReference.push().setValue(message);

                editTextMessage.setText(CLEAR_EDIT_TEXT);
            }
        });
    }

    private void handlerEditText() {
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // метод работает при изменении текста Edit Text
                if (charSequence.toString().trim().length() > 0) {
                    // разрешаем нажатие кнопки если хоть что-то было введено
                    buttonSendMessage.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // ограничиваем вводимость 500  символами
        editTextMessage.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_LENGTH_MESSAGE)
        });
    }

    private void initFareBaseDB() {

        // получаем доступ к корневой папке БД
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        // ссылка на узел БД
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

        // ссылаемся на папку которая создана в storage firebase
        storageReference = firebaseStorage.getReference().child("chat_images");

    }

    private void getDataFromSignInActivity(){
        Intent currentIntent = getIntent();
        if(currentIntent != null){
            userName = currentIntent.getStringExtra(NICKNAME);
            System.out.println(userName);
        }else {
            userName = "Default User";
        }
    }

    private void handlerMessagesChildEventListener() {
        // обработчик всех изменений в базе данных
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // срабатывает при добавлении элемента

                // получаем данные и помещаем их в класс
                AwesomeMessage message = snapshot.getValue(AwesomeMessage.class);
                adapter.add(message);// добавляем в адаптер класс

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

    private void handlerUsersChildEventListener() {
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
                    userName = user.getName();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch (id){
            case R.id.sign_out:
                // разлогиниваемся в FireBase
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this , SignInActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    данный метод вызывается после выбора
    изображения пользователем через метод : handlerImageButtonSendPhoto
     */
    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        if(requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
            // получаем Uri изображения
            Uri selectedImageUri = data.getData();
            // получаем последний сегмент изображения
            StorageReference imageReference = storageReference
                    .child(selectedImageUri.getLastPathSegment());

            uploadFile(imageReference, selectedImageUri);
        }
    }

    private void uploadFile(final StorageReference imageReference , Uri selectedImageUri){
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
                    awesomeMessage.setName(userName);
                    awesomeMessage.setText(editTextMessage.getText().toString());
                    editTextMessage.setText(CLEAR_EDIT_TEXT);
                    // отправляем в DB
                    messagesDatabaseReference.push().setValue(awesomeMessage);
                } else {

                }
            }
        });
    }
}