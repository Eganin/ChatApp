package com.example.chatapp.mvp.signin;


import androidx.annotation.NonNull;

import com.example.chatapp.common.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.chatapp.contacts.ContactException.ToastText.*;

public class SignInModel {

    public static FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;

    private SignInPresenter presenter;

    public SignInModel(){
        // init presenter
        presenter = new SignInPresenter(this);
    }

    interface initCallBack{
        void init();
    }

    interface loginComplete{
        void startView();
    }

    interface  loginFailed{
        void setToast();
    }

    public void initDB(initCallBack init){
        firebaseAuth = FirebaseAuth.getInstance();// get instance db
        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");
    }

    public void loginOrSignUpUser(final loginComplete loginComplete , final loginFailed loginFailed,
                                  final Task<AuthResult> firebaseAuthMethod,
                                  final boolean isNewUser , SignInView view){
        // метод отвечает за добавление пользователя или входа в Firebase
        firebaseAuthMethod.addOnCompleteListener(view, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // добавление пользователя
                    FirebaseUser user = firebaseAuth.getCurrentUser();// получение текущего пользователя
                    if(isNewUser){
                        // если мы регистрируем пользователя
                        createUser(user);
                    }
                    loginComplete.startView();

                } else {
                    // если не удалось войти или зарегистртроваться
                    loginFailed.setToast();
                }
            }
        });
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
        user.setName(SignInPresenter.name);

        //отправляем объект в базу
        usersDatabaseReference.push().setValue(user);
    }

    public void checkCurrentUser(initCallBack init){
        // метод для уже залогининных пользователей отправляет в MainActivity
        // получаем текущего пользователя
        if (firebaseAuth.getCurrentUser() != null) {
            init.init();
        }
    }
}
