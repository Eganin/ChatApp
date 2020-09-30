package com.example.chatapp.mvp.menu.customize;

import android.content.Intent;
import android.net.Uri;

import com.example.chatapp.common.NewUserData;

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

    public void deleteUser(NewUserData newUserData) {

    }
}
