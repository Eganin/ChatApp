package com.example.chatapp.mvp.menu.customize;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CustomizeModel {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public static Bitmap bitmap ;

    public interface setImage{
        void set();
    }

    public interface init{
        void inits();
    }

    public void initDB(){
        auth = FirebaseAuth.getInstance();

        // получаем доступ к корневой папке БД
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        // ссылка на узел БД

        // ссылаемся на папку которая создана в storage firebase
        storageReference = firebaseStorage.getReference().child("avatar_images");
    }

    public void getReferenceFromImage(setImage setImage ,Uri imageUri , CustomizeView view){
        StorageReference imageReference = storageReference
                .child(imageUri.getLastPathSegment());

        uploadFile(imageReference, imageUri , view);

        if(setImage != null){
            setImage.set();
        }

    }

    private void uploadFile(final StorageReference imageReference, Uri imageUri ,
                            final CustomizeView view) {
        // загружаем локальный файл-изображение в firebase storage

        // загружаем файл
        UploadTask uploadTask = imageReference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,
                Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    // если не получилось занрузить файл
                    throw task.getException();
                }

                return imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // получаем ссылку изображения котрое потом скачаем с помощью Glide
                    Uri downloadUri = task.getResult();
                    downloadImageAvatar(view,downloadUri);
                } else {

                }
            }
        });
    }

    private void downloadImageAvatar(final CustomizeView view , Uri downloadUri) {
        /*Glide.with(view.circularImageView.getContext())
                .load(downloadUri)
                .into(view.circularImageView);*/

        Glide.with(view)
                .load(downloadUri)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.circularImageView.setImageBitmap(resource);
                        bitmap = resource;
                        view.permission(new init() {
                            @Override
                            public void inits() {

                            }
                        });
                    }
                });

    }

}
