package com.example.chatapp.mvp.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.adapter.AwesomeMessageAdapter;
import com.example.chatapp.common.AwesomeMessage;
import com.example.chatapp.common.DataMessage;
import com.example.chatapp.mvp.signin.SignInView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static com.example.chatapp.contacts.ContactException.IntentKeys.NICKNAME;
import static com.example.chatapp.contacts.ContactException.IntentKeys.RECIPIENT_USER_ID;
import static com.example.chatapp.contacts.ContactException.IntentKeys.RECIPIENT_USER_NAME;
import static com.example.chatapp.contacts.ContactException.Text.CLEAR_EDIT_TEXT;
import static com.example.chatapp.contacts.ContactException.Types.MAX_LENGTH_MESSAGE;
import static com.example.chatapp.contacts.ContactException.Types.RC_IMAGE_PICKER;

public class ChatView extends AppCompatActivity {

    private ListView listView;
    private AwesomeMessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton imageButtonSendPhoto;
    private Button buttonSendMessage;
    private EditText editTextMessage;

    public String userName;
    private String recipientUserId;
    private String recipientUserName;

    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initPresenter();
        initDB();
        findView();
        getDataFromUserListActivity();
        handlerEditText();
        handlerButtonSendMessage();
        //handlerImageButtonSendPhoto();
        handlerMessagesChildEventListener();
        handlerUsersChildEventListener();
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
                startActivity(new Intent(ChatView.this , SignInView.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        presenter.detachView();
    }

    private void initPresenter() {
        ChatModel chatModel = new ChatModel();
        presenter = new ChatPresenter(chatModel);
        presenter.attachView(this);

    }

    private void initDB(){
        presenter.initDB(this);
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
                presenter.newIntentForImageChoose();
            }
        });
    }

    /*
    данный метод вызывается после выбора
    изображения пользователем через метод : handlerImageButtonSendPhoto
     */
    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        if(requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK){
            presenter.downloadFile(data);
        }
    }

    private void getDataFromUserListActivity(){
        Intent intent = getIntent();
        if(intent != null){
            recipientUserId =intent.getStringExtra(RECIPIENT_USER_ID);
            recipientUserName = intent.getStringExtra(RECIPIENT_USER_NAME);
            userName = intent.getStringExtra(NICKNAME);
            setTitle(recipientUserName);
        }
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

    private void handlerButtonSendMessage(){
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.sendMessage(getDataFromMessage(), ChatView.this);
            }
        });
    }

    public DataMessage getDataFromMessage(){
        String textMessage = editTextMessage.getText().toString();
        String userName = this.userName;
        String recipientId = this.recipientUserId;

        DataMessage dataMessage = new DataMessage(textMessage,userName,recipientId);

        return dataMessage;
    }

    private void handlerMessagesChildEventListener(){
        presenter.listenerMessages(this);
    }

    private void handlerUsersChildEventListener(){
        presenter.listenerUsers();
    }

    public void clearEditText(){
        editTextMessage.setText(CLEAR_EDIT_TEXT);
    }

    public void interactionWithAdapter(AwesomeMessage message) {
        adapter.add(message);
    }
}
