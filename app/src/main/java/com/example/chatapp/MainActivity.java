package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CLEAR_EDIT_TEXT = "";
    private static final int MAX_LENGTH_MESSAGE = 500;

    private ListView listView;
    private AwesomeMessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton imageButtonSendPhoto;
    private Button buttonSendMessage;
    private EditText editTextMessage;

    private String userName;

    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    ChildEventListener messagesChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFareBaseDB();
        findView();
        handlerEditText();
        handlerButtonSendMessage();
        handlerImageButtonSendPhoto();
        handlerChildEventListener();
    }

    private void findView() {
        progressBar = findViewById(R.id.progressBar);
        imageButtonSendPhoto = findViewById(R.id.imageButtonSendPhoto);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        editTextMessage = findViewById(R.id.editTextMessage);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        initListView();
    }

    private void initListView() {
        userName = "Default";

        List<AwesomeMessage> awesomeMessages = new ArrayList<AwesomeMessage>();
        listView = findViewById(R.id.listView);
        adapter = new AwesomeMessageAdapter(this, R.layout.message_item,
                awesomeMessages);

        listView.setAdapter(adapter);
    }


    private void handlerImageButtonSendPhoto() {
        imageButtonSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void handlerButtonSendMessage() {
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AwesomeMessage message = new AwesomeMessage();
                message.setText(editTextMessage.getText().toString());
                System.out.println(editTextMessage.getText().toString());
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

        // ограничиваем вводимость символов
        editTextMessage.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_LENGTH_MESSAGE)
        });
    }

    private void initFareBaseDB() {

        // получаем доступ к корневой папке БД
        database = FirebaseDatabase.getInstance();
        // ссылка на новый узел БД
        messagesDatabaseReference = database.getReference().child("messages");

    }

    private void handlerChildEventListener(){
        // обработчик изменений в базе данных
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // срабатывает при добавлении элемента
                AwesomeMessage message = snapshot.getValue(AwesomeMessage.class);
                System.out.println(message.getText().toString());
                adapter.add(message);

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
}