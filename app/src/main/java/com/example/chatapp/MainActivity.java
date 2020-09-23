package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.chatapp.ContactException.IntentKeys.*;

public class MainActivity extends AppCompatActivity {

    private static final String CLEAR_EDIT_TEXT = "";
    private static final int MAX_LENGTH_MESSAGE = 500;
    private static final String MIME_TYPE_IMAGES = "image/jpeg";
    private static final String TEXT_INTENT_IMAGE = "Выберите изображение";
    private static final Integer RC_IMAGE_PICKER = 123;

    private ListView listView;
    private AwesomeMessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton imageButtonSendPhoto;
    private Button buttonSendMessage;
    private EditText editTextMessage;

    public static String userName;

    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    ChildEventListener messagesChildEventListener;

    DatabaseReference usersDatabaseReference;
    ChildEventListener usersChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // ссылка на узел БД
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

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
                startActivity(new Intent(MainActivity.this , SignInActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}