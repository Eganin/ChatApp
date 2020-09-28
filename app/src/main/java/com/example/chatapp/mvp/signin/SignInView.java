package com.example.chatapp.mvp.signin;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

import com.example.chatapp.common.InfoUserSignIn;

import com.example.chatapp.exception.NoInfoFromEditTextException;
import com.example.chatapp.mvp.userlist.UserListView;

import static com.example.chatapp.contacts.ContactException.IntentKeys.NICKNAME;
import static com.example.chatapp.contacts.ContactException.ToggleLoginModeTexts.LOG_IN;
import static com.example.chatapp.contacts.ContactException.ToggleLoginModeTexts.SIGN_UP;
import static com.example.chatapp.contacts.ContactException.ToggleLoginModeTexts.SWITCH_LOG_IN;
import static com.example.chatapp.contacts.ContactException.ToggleLoginModeTexts.SWITCH_SIGN_UP;

public class SignInView extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNickName;
    private EditText editTextPasswordRepeat;
    private Button buttonSignUp;
    private TextView toggleLoginSignUpTextView;

    private String nickName;

    private boolean loginModeActive; // регистрируется или логируется пользователь

    private SignInPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initPresenter();
        initDB();
        checkCurrentUser();
        findView();
        buttonHandler();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        presenter.detachView();
    }

    private void initPresenter(){
        SignInModel signInModel = new SignInModel();
        presenter = new SignInPresenter(signInModel);
        presenter.attachView(SignInView.this);
    }

    private void checkCurrentUser() {
        presenter.checkCurrentUser();
    }



    private void initDB(){
        presenter.initDB(SignInView.this);
    }

    private void findView() {
        /*
        ищем все действующие поля views
         */
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNickName = findViewById(R.id.editTextNickName);
        editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView);
    }

    private void buttonHandler() {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.signUpUser(loginModeActive);

            }
        });
    }

    public InfoUserSignIn getInfoFromUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeatPassword = editTextPasswordRepeat.getText().toString().trim();
        nickName = editTextNickName.getText().toString().trim();

        InfoUserSignIn infoUserSignIn = new InfoUserSignIn(nickName,email,password,repeatPassword);

        return infoUserSignIn;
    }



    public void toggleLoginMode(View view) {
        /*
        Метод менят интерфейс с помощью TextView
        между регистрацией и входом
         */
        if (loginModeActive) {
            loginModeActive = false;
            buttonSignUp.setText(SIGN_UP);
            toggleLoginSignUpTextView.setText(SWITCH_LOG_IN);
            editTextPasswordRepeat.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            buttonSignUp.setText(LOG_IN);
            toggleLoginSignUpTextView.setText(SWITCH_SIGN_UP);
            editTextPasswordRepeat.setVisibility(View.GONE);
        }
    }

    public void showToastException(NoInfoFromEditTextException exception){
        Toast.makeText(SignInView.this, exception.getMessageException()
                , Toast.LENGTH_LONG).show();
    }

    public void showToast(String text){
        Toast.makeText(SignInView.this,
                text, Toast.LENGTH_LONG).show();
    }

    public void startIntentUserListView(){
        Intent intent = new Intent(SignInView.this , UserListView.class);
        intent.putExtra(NICKNAME,nickName);
        startActivity(intent);
    }

}
