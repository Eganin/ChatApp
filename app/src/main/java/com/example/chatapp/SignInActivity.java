package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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
    private Button buttonSignUp;
    private TextView toggleLoginSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findView();
        firebaseAuth = FirebaseAuth.getInstance();

        buttonHandler();
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    private void findView(){
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNickName = findViewById(R.id.editTextNickName);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView);
    }

    private void buttonHandler(){
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair<String , String> pairEditTextResult;
                try{
                    if((pairEditTextResult = checkingValuesEditTexts()) != null){
                        loginSignUpUser(pairEditTextResult.first , pairEditTextResult.second);
                    }
                }catch (NoInfoFromEditTextException e){
                    Toast.makeText(SignInActivity.this, TOAST_MESSAGE_NULL_EDIT_TEXT
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Pair<String , String> checkingValuesEditTexts() throws NoInfoFromEditTextException {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(!email.equals("") && !password.equals("")){
            return new Pair<String , String>(email , password);
        }else{
            throw  new NoInfoFromEditTextException();
        }
    }

    private void loginSignUpUser(String email , String password) {
        // метод отвечает за добавление пользоватеоя в Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            // добавление пользователя
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this,
                                    "Authentication complete", Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}