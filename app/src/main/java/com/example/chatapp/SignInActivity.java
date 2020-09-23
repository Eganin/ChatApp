package com.example.chatapp;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final String TOAST_MESSAGE_NULL_EDIT_TEXT = "Заполните данные поля";

    FirebaseAuth firebaseAuth;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNickName;
    private EditText editTextPasswordRepeat;
    private Button buttonSignUp;
    private TextView toggleLoginSignUpTextView;

    private boolean loginModeActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();// get instance db
        checkCurrentUser();
        findView();
        buttonHandler();
    }

    private void findView() {
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
        String nickName = editTextNickName.getText().toString().trim();

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
        if (loginModeActive) {
            // вызываем метод для проверки и входа пользователя
            loginOrSignUpUser(firebaseAuth.signInWithEmailAndPassword(email, password));
        } else {
            // вызываем метод для регистрации пользователя
            loginOrSignUpUser(firebaseAuth.createUserWithEmailAndPassword(email, password));
        }
    }

    public void toggleLoginMode(View view) {
        if (loginModeActive) {
            loginModeActive = false;
            buttonSignUp.setText("Sign Up");
            toggleLoginSignUpTextView.setText("Tap to Log In");
            editTextPasswordRepeat.setVisibility(View.VISIBLE);
            editTextNickName.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            buttonSignUp.setText("Log In");
            toggleLoginSignUpTextView.setText("Tap to Sign Up");
            editTextPasswordRepeat.setVisibility(View.GONE);
            editTextNickName.setVisibility(View.GONE);
        }
    }

    private void loginOrSignUpUser(Task<AuthResult> firebaseAuthMethod) {
        // метод отвечает за добавление пользователя или входа в Firebase
        firebaseAuthMethod.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // добавление пользователя
                    FirebaseUser user = firebaseAuth.getCurrentUser();// получение текущего пользователя
                    Toast.makeText(SignInActivity.this,
                            "Authentication complete", Toast.LENGTH_LONG).show();
                    //updateUI(user);
                    startActivity(new Intent(SignInActivity.this,
                            MainActivity.class));
                } else {
                    // если не удалось войти тлт зарегистртроваться
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_LONG).show();
                    //updateUI(null);
                }
            }
        });
    }

    private void checkCurrentUser() {
        // метод для уже залогининных пользователей отправляет в MainActivity
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }
    }

    private void handlerException(NoInfoFromEditTextException exception) {
        Toast.makeText(SignInActivity.this, exception.getMessageException()
                , Toast.LENGTH_LONG).show();
    }
}