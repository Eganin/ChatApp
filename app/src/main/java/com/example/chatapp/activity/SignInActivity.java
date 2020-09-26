package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.contacts.ContactException;
import com.example.chatapp.exception.NoInfoFromEditTextException;
import com.example.chatapp.R;
import com.example.chatapp.common.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.chatapp.contacts.ContactException.IntentKeys.*;
import static com.example.chatapp.contacts.ContactException.TAG.*;
import static com.example.chatapp.contacts.ContactException.ToggleLoginModeTexts.*;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;


    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNickName;
    private EditText editTextPasswordRepeat;
    private Button buttonSignUp;
    private TextView toggleLoginSignUpTextView;

    private String nickName;

    private boolean loginModeActive; // регистрируется или логируется пользователь
    private boolean isNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initDB();
        checkCurrentUser();
        findView();
        buttonHandler();
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
                Pair<String, String> pairEditTextResult;
                try {
                    // проыеряем что ввел пользователь
                    if ((pairEditTextResult = checkingValuesEditTexts()) != null) {
                        loginSignUpUser(pairEditTextResult.first, pairEditTextResult.second);
                    }
                } catch (NoInfoFromEditTextException e) {
                    handlerException(e);
                }
            }
        });
    }

    private Pair<String, String> checkingValuesEditTexts() throws NoInfoFromEditTextException {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeatPassword = editTextPasswordRepeat.getText().toString().trim();
        nickName = editTextNickName.getText().toString().trim();

        if (loginModeActive) {
            if (email.equals("")) {
                throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_EMAIL);
            }
            if (password.equals("")) {
                throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_PASSWORD);
            }
            return new Pair<String, String>(email, password);
        }
        if (!email.equals("") && !password.equals("") && repeatPassword.equals(password)) {
            return new Pair<String, String>(email, password);
        }
        if (nickName.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_NICKNAME);
        }
        if (email.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_EMAIL);
        }
        if (password.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_PASSWORD);
        }
        if (password.length() <= 6) {
            throw new NoInfoFromEditTextException(ContactException.Exception.LENGTH_PASSWORD);
        }
        if (repeatPassword.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_REPEAT_PASSWORD);
        }
        if (!repeatPassword.equals(password)) {
            throw new NoInfoFromEditTextException(ContactException.Exception.PASSWORD_DONT_MATCH);
        } else {
            throw new NoInfoFromEditTextException(ContactException.Exception.UNKNOWN);
        }
    }

    private void loginSignUpUser(String email, String password) {
        // метод отвечает за добавление пользователя в Firebase
        // проверяем регистрируется или логинится пользователь
        if (loginModeActive) {
            isNewUser = false;
            // sign in user
            loginOrSignUpUser(firebaseAuth.signInWithEmailAndPassword(email, password),isNewUser);
        } else {
            isNewUser = true;
            // create authorization user
            loginOrSignUpUser(firebaseAuth.createUserWithEmailAndPassword(email, password),isNewUser);
        }
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

    private void loginOrSignUpUser(final Task<AuthResult> firebaseAuthMethod, final boolean isNewUser) {
        // метод отвечает за добавление пользователя или входа в Firebase
        firebaseAuthMethod.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // добавление пользователя
                    FirebaseUser user = firebaseAuth.getCurrentUser();// получение текущего пользователя
                    if(isNewUser){
                        // если мы регистрируем пользователя
                        createUser(user);
                    }
                    Toast.makeText(SignInActivity.this,
                           "Authentication complete", Toast.LENGTH_LONG).show();
                    startIntent();
                } else {
                    // если не удалось войти или зарегистртроваться
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startIntent(){
        Intent intent = new Intent(SignInActivity.this , UserListActivity.class);
        intent.putExtra(NICKNAME,nickName);
        startActivity(intent);
    }

    private void checkCurrentUser() {
        // метод для уже залогининных пользователей отправляет в MainActivity
        // получаем текущего пользователя
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, UserListActivity.class));
        }
    }

    private void handlerException(NoInfoFromEditTextException exception) {
        // обрабатываем свою ошибку NoInfoFromEditTextException
        Toast.makeText(SignInActivity.this, exception.getMessageException()
                , Toast.LENGTH_LONG).show();
    }

    private void createUser(FirebaseUser firebaseUser){
        /*
        создаем объект User для добавления в БД
         */
        User user = new User();
        // вставляем id пользователя
        user.setId(firebaseUser.getUid());
        // вставляем Email пользователя
        user.setEmail(firebaseUser.getEmail());
        // вставляем nickname пользователя
        user.setName(editTextNickName.getText().toString());

        //отправляем объект в базу
        usersDatabaseReference.push().setValue(user);
    }

    private void initDB(){
        firebaseAuth = FirebaseAuth.getInstance();// get instance db
        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");
    }
}