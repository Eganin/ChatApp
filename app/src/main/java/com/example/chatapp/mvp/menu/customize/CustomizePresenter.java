package com.example.chatapp.mvp.menu.customize;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.example.chatapp.common.NewUserData;
import com.example.chatapp.common.User;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.example.chatapp.contacts.ContactException.Text.TEXT_INTENT_IMAGE;
import static com.example.chatapp.contacts.ContactException.Types.MIME_TYPE_IMAGES;
import static com.example.chatapp.contacts.ContactException.Types.RC_IMAGE_PICKER;
import static com.example.chatapp.mvp.menu.customize.CustomizeModel.bitmap;

public class CustomizePresenter {

    private CustomizeModel model;
    private CustomizeView view;

    public CustomizePresenter(CustomizeModel model) {
        this.model = model;
    }

    public void attachView(CustomizeView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void initDB(CustomizeView view) {
        model.initDB();
        attachView(view);
    }

    public void newIntentForImageChoose() {
        // intent для получения контента-изображения
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME_TYPE_IMAGES);// mime type
        // получаем изображения с локального хранилища
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        view.startActivityForResult(Intent.createChooser(intent, TEXT_INTENT_IMAGE),
                RC_IMAGE_PICKER);
    }

    public void downloadFile(Intent data, final CustomizeView view) {
        Uri selectedImage = data.getData();
        model.getReferenceFromImage(new CustomizeModel.setImage() {
                                        @Override
                                        public void set() {
                                            view.GONEImageViewNotImage();

                                        }
                                    }, selectedImage,
                                        this.view);
    }

    public void checkPermission(CustomizeModel.init init){
        if (ActivityCompat.checkSelfPermission(view, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(view, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                /*
            если нет нужного разрешения у приложения
            то запрашиваем  у пользователя разрешение на совершение звонков
             */
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(view, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    public Uri getUriImage(Bitmap inImage){
        /*
        Метод отвечает за получение Uri у
        изображения скачаного с помощью Glide
         */
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(view.getContentResolver(),
                inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }

    public void deleteUserAndCreateUser(User user,String password , String repeatPassword) {
        model.deleteUserInDB();
        model.deleteUser(repeatPassword);
        attachView(view);
        model.createUser(user , password , view);
    }

    public String getCurrentId(){
        String UID =  model.getCurrentUIDId();
        attachView(view);
        return UID;
    }

    public void viewToastInView(){
        attachView(view);
        view.viewToast();
    }
}
