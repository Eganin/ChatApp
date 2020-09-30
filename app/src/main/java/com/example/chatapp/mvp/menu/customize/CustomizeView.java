package com.example.chatapp.mvp.menu.customize;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chatapp.R;
import com.example.chatapp.common.NewUserData;
import com.pkmmte.view.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.example.chatapp.contacts.ContactException.Types.RC_IMAGE_PICKER;

public class CustomizeView extends AppCompatActivity {

    public CircularImageView circularImageView;
    private ImageView imageViewNotImage;
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
            } else {
                System.out.println("AAAAAAAAAAAAAAAAAAA ");
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
        editTextCustomizeEmail = findViewById(R.id.editTExtCustomizeEmail);
        editTextCustomizeNickName = findViewById(R.id.editTextCustomizeNickName);
        editTextCustomizePassword = findViewById(R.id.editTExtCustomizePassword);
        editTextUserInfo = findViewById(R.id.editTextUserInfo);
        buttonSubmitCustomize = findViewById(R.id.buttonSubmitCustomize);
    }

    private void initDB() {
        presenter.initDB(CustomizeView.this);
    }


    private NewUserData getDataFromNewUser() {
        String email = editTextCustomizeEmail.getText().toString();
        String password = editTextCustomizePassword.getText().toString();
        String textUserInfo = editTextUserInfo.getText().toString();
        String nickName = editTextCustomizeNickName.getText().toString();
        NewUserData newUserData = new NewUserData(null, email, password, textUserInfo,
                nickName);

        return newUserData;
    }

    private void handlerButton() {
        buttonSubmitCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.deleteUser(getDataFromNewUser());
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            /*
            если нет нужного разрешения у приложения
            то запрашиваем  у пользователя разрешение на совершение звонков
             */
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        System.out.println(getImageUri(getApplicationContext(), CustomizeModel.bitmap)+"---------------------------");
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(CustomizeView.this.getContentResolver(),
                inImage, UUID.randomUUID().toString() + ".png", "drawing");
        //MediaStore.Images.Media.getBitmap(inContext.getContentResolver() ,);
        return Uri.parse(path);

    }


}
