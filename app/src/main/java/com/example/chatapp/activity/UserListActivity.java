package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatapp.R;
import com.example.chatapp.adapter.UserAdapter;
import com.example.chatapp.common.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.chatapp.contacts.ContactException.IntentKeys.*;
import static com.example.chatapp.contacts.ContactException.NameFavoriteUser.*;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private DatabaseReference usersDatabaseReference;
    private ChildEventListener userChildEventListener;

    private FirebaseAuth auth;

    private ArrayList<User> userArrayList;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        auth = FirebaseAuth.getInstance();

        getDataFromSignInActivity();
        initRecyclerView();
        implementsInterfaceFromUserAdapter();
        attachUserDatabaseReferenceListener();
    }

    private void attachUserDatabaseReferenceListener() {
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

    private void initRecyclerView(){

        recyclerView = findViewById(R.id.recyclerViewUserList);
        userArrayList = new ArrayList<User>();
        userAdapter = new UserAdapter(userArrayList);
        layoutManager = new LinearLayoutManager(this);

        // добавляем програмно в низу user_item.xml черту
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);

    }

    private void implementsInterfaceFromUserAdapter(){
        userAdapter.setOnClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                goToChat(position);
            }
        });
    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this , ChatActivity.class);
        intent.putExtra(RECIPIENT_USER_ID,userArrayList.get(position).getId());
        intent.putExtra(RECIPIENT_USER_NAME,userArrayList.get(position).getName());
        intent.putExtra(NICKNAME , userName);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch (id){
            case R.id.sign_out:
                // разлогиниваемся в FireBase
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserListActivity.this , SignInActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getDataFromSignInActivity(){
        Intent intent = getIntent();
        userName = intent.getStringExtra(NICKNAME);
    }
}