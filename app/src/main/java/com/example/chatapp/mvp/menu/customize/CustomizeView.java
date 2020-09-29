package com.example.chatapp.mvp.menu.customize;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class CustomizeView extends AppCompatActivity {

    private ImageView imageViewAvatar;
    private EditText editTextCustomizeNickName;
    private EditText editTextCustomizePassword;
    private EditText editTextCustomizeEmail;
    private EditText editTextUserInfo;
    private Button buttonSubmitCustomize;
    private CustomizePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_customize);

        initPresenter();
        findFields();
        handlerButton();
    }

    private void initPresenter(){
        CustomizeModel customizeModel = new CustomizeModel();
        presenter = new CustomizePresenter(customizeModel);
    }

    private void findFields(){
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        editTextCustomizeEmail = findViewById(R.id.editTExtCustomizeEmail);
        editTextCustomizeNickName = findViewById(R.id.editTextCustomizeNickName);
        editTextCustomizePassword = findViewById(R.id.editTExtCustomizePassword);
        editTextUserInfo = findViewById(R.id.editTextUserInfo);
        buttonSubmitCustomize = findViewById(R.id.buttonSubmitCustomize);
    }

    private void handlerButton(){
        buttonSubmitCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
