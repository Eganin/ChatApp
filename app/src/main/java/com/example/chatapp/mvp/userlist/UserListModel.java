package com.example.chatapp.mvp.userlist;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.adapter.UserAdapter;
import com.example.chatapp.common.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.chatapp.contacts.ContactException.NameFavoriteUser.FAVORITE;

public class UserListModel {

    private DatabaseReference usersDatabaseReference;
    private ChildEventListener userChildEventListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference imagesStorage;
    private UserAdapter.UserViewHolder holder;
    private FirebaseAuth auth;
    private UserListView view;

    public void initDB(){
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        imagesStorage = firebaseStorage.getReference().child("avatar_user_list");
    }

    public void setHolder(UserAdapter.UserViewHolder holder){
        this.holder=holder;
    }

    public void setView(UserListView view){
        this.view=view;
    }

    public void signOutDB(){
        FirebaseAuth.getInstance().signOut();
    }


    public void downloadImage(final String nameImage ){
        final ImageView imageView = holder.imageViewAvatarUser;
        initDB();
        String imageURL = imagesStorage.child(nameImage).getDownloadUrl().toString();
        Glide.with(view.recyclerView.getContext()).load(imageURL).into(imageView);


        /*imagesStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = imagesStorage.child(nameImage).getDownloadUrl().toString();
                Glide.with(view).load(imageURL).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/
    }

    public void listenerDBUsers( final ArrayList<User> userArrayList,
                                final  UserAdapter userAdapter){
        // обработка изменений в DB в узде users
        // ссылка на узел в DB
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if(userChildEventListener == null){
            userChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot,
                                         @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);

                    if(!user.getId().equals(auth.getCurrentUser().getUid())){
                        // добавляем всех пользователей в RecyclerView кроме своего акаунта
                        user.setAvatarMockUpResource(R.drawable.ic_baseline_person_add_24);
                        userArrayList.add(user);
                        userAdapter.notifyDataSetChanged(); // update recycler view
                    }else if(user.getId().equals(auth.getCurrentUser().getUid())){
                        // свой аккаунт меняем на переписку в избранном
                        user.setAvatarMockUpResource(R.drawable.ic_baseline_person_pin_24);
                        user.setName(FAVORITE);
                        userArrayList.add(user);
                        userAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot,
                                           @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot,
                                         @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            // добавляем обработчик
            usersDatabaseReference.addChildEventListener(userChildEventListener);
        }
    }
}
