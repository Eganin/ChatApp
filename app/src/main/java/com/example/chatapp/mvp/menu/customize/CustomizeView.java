package com.example.chatapp.mvp.menu.customize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.common.User;
import com.pkmmte.view.CircularImageView;

import static com.example.chatapp.contacts.ContactException.Types.RC_IMAGE_PICKER;

public class CustomizeView extends AppCompatActivity {

    public CircularImageView circularImageView;
    private ImageView imageViewNotImage;
    private EditText editTextCustomizeNickName;
    private EditText editTextCustomizePassword;
    private EditText editTextCustomizeEmail;
    private EditText editTextUserInfo;
    private EditText editTextCustomizeRepeatPassword;
    private EditText editTextCustomizeLastNickName;
    private Button buttonSubmitCustomize;
    private CustomizePresenter presenter;

    private String password;
    private String repeatPassword;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_customize);

        initPresenter();
        initDB();
        findFields();
        initCircularImageView();
        handlerButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            presenter.downloadFile(data, CustomizeView.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission(new CustomizeModel.init() {
                    @Override
                    public void inits() {

                    }
                });
            }
        }
    }

    private void initPresenter() {
        CustomizeModel customizeModel = new CustomizeModel();
        presenter = new CustomizePresenter(customizeModel);
        presenter.attachView(CustomizeView.this);
    }

    private void initCircularImageView() {
        circularImageView.setBorderWidth(10);
        circularImageView.setSelectorStrokeWidth(10);
        circularImageView.addShadow();
    }

    private void findFields() {
        circularImageView = findViewById(R.id.imageViewAvatar);
        imageViewNotImage = findViewById(R.id.imageViewNotImage);
        editTextCustomizeEmail = findViewById(R.id.editTextCustomizeEmail);
        editTextCustomizeNickName = findViewById(R.id.editTextCustomizeNickName);
        editTextCustomizePassword = findViewById(R.id.editTextCustomizePassword);
        editTextCustomizeRepeatPassword = findViewById(R.id.editTextCustomizeRepeatPassword);
        editTextUserInfo = findViewById(R.id.editTextUserInfo);
        editTextCustomizeLastNickName = findViewById(R.id.editTextCustomizeLastNickName);
        buttonSubmitCustomize = findViewById(R.id.buttonSubmitCustomize);
    }

    private void initDB() {
        presenter.initDB(CustomizeView.this);
    }


    private User getDataFromNewUser() {
        String id = presenter.getCurrentId();
        Uri uriImage = presenter.getUriImage(CustomizeModel.bitmap);
        String email = editTextCustomizeEmail.getText().toString();
        password = editTextCustomizePassword.getText().toString();
        repeatPassword = editTextCustomizeRepeatPassword.getText().toString();
        lastName = editTextCustomizeLastNickName.getText().toString();
        String textUserInfo = editTextUserInfo.getText().toString();
        String nickName = editTextCustomizeNickName.getText().toString();
        User user = new User(nickName,email,id,String.valueOf(uriImage),textUserInfo);

        return user;
    }

    private void handlerButton() {
        buttonSubmitCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.deleteUserAndCreateUser(getDataFromNewUser(),password, repeatPassword,
                        lastName);
            }
        });
    }

    public void handlerImageUser(View view) {
        presenter.newIntentForImageChoose();
    }

    public void GONEImageViewNotImage() {
        imageViewNotImage.setVisibility(View.GONE);
    }

    public void permission(CustomizeModel.init init) {
        presenter.checkPermission(init);
    }

    public void viewToast(){
        Toast.makeText(CustomizeView.this, "User deleted", Toast.LENGTH_LONG).show();
    }



}
