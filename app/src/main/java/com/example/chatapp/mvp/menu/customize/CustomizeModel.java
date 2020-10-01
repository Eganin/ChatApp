package com.example.chatapp.mvp.menu.customize;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.chatapp.common.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.chatapp.mvp.signin.SignInModel.firebaseAuth;

public class CustomizeModel {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private CustomizePresenter presenter;

    public static Bitmap bitmap;

    public CustomizeModel() {
        presenter = new CustomizePresenter(CustomizeModel.this);
    }

    public interface setImage {
        void set();
    }

    public interface init {
        void inits();
    }

    public interface startUserListView{
        void start();
    }

    public void initDB() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        usersDatabaseReference = database.getReference().child("users");
        // ссылаемся на папку которая создана в storage firebase
        storageReference = firebaseStorage.getReference().child("avatar_images");
    }

    public void getReferenceFromImage(setImage setImage, Uri imageUri, CustomizeView view) {
        StorageReference imageReference = storageReference
                .child(imageUri.getLastPathSegment());

        uploadFile(imageReference, imageUri, view);

        if (setImage != null) {
            setImage.set();
        }

    }

    private void uploadFile(final StorageReference imageReference, Uri imageUri,
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
                    downloadImageAvatar(view, downloadUri);
                } else {

                }
            }
        });
    }

    private void downloadImageAvatar(final CustomizeView view, Uri downloadUri) {
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

    public void deleteUser(String repeatPassword) {
        final FirebaseUser user = auth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), repeatPassword);

        auth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //presenter.viewToastInView();
                                }
                            }
                        });
                    }
                });
        auth.getCurrentUser().delete();
    }

    public String getCurrentUIDId() {
        String uid = auth.getCurrentUser().getUid();

        return uid;
    }

    public void deleteUserInDB(String lastName) {

        Query query = usersDatabaseReference.orderByChild("name").equalTo(lastName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createUser(final startUserListView startUserListView, final User user, String password, CustomizeView view) {
        String email = user.getEmail();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(view, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // добавление пользователя
                    FirebaseUser userFireBase = firebaseAuth.getCurrentUser();// получение текущего пользователя
                    user.setId(userFireBase.getUid());
                    user.setEmail(userFireBase.getEmail());
                    usersDatabaseReference.push().setValue(user);

                    if (startUserListView != null){
                        startUserListView.start();
                    }

                } else {

                }
            }
        });
    }

}
