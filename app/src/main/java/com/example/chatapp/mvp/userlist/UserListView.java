package com.example.chatapp.mvp.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapter.UserAdapter;
import com.example.chatapp.common.User;
import com.example.chatapp.mvp.chat.ChatView;
import com.example.chatapp.mvp.menu.customize.CustomizeView;
import com.example.chatapp.mvp.menu.settings.SettingsView;
import com.example.chatapp.mvp.signin.SignInView;

import java.util.ArrayList;

import static com.example.chatapp.contacts.ContactException.IntentKeys.NICKNAME;
import static com.example.chatapp.contacts.ContactException.IntentKeys.RECIPIENT_USER_ID;
import static com.example.chatapp.contacts.ContactException.IntentKeys.RECIPIENT_USER_NAME;

public class UserListView extends AppCompatActivity {

    private ArrayList<User> userArrayList;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String userName;

    private UserListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user_list);

        getDataFromSignInActivity();
        initPresenter();
        initDB();
        initRecyclerView();
        implementsInterfaceFromUserAdapter();
        attachUserDatabaseReferenceListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.sign_out:
                // разлогиниваемся в FireBase
                presenter.signOutDB();
                startActivity(new Intent(UserListView.this, SignInView.class));
                return true;

            case R.id.settings:
                startActivity(new Intent(UserListView.this, SettingsView.class));
                return true;


            case R.id.customize_user:
                startActivity(new Intent(UserListView.this, CustomizeView.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getDataFromSignInActivity() {
        Intent intent = getIntent();
        userName = intent.getStringExtra(NICKNAME);
    }

    private void initPresenter() {
        UserListModel userListModel = new UserListModel();
        presenter = new UserListPresenter(userListModel);
        presenter.attachView(this);

    }

    private void initDB() {
        presenter.initDB();
    }

    private void initRecyclerView() {
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

    private void implementsInterfaceFromUserAdapter() {
        userAdapter.setOnClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                goToChat(position);
            }
        });
    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListView.this, ChatView.class);
        intent.putExtra(RECIPIENT_USER_ID, userArrayList.get(position).getId());
        intent.putExtra(RECIPIENT_USER_NAME, userArrayList.get(position).getName());
        intent.putExtra(NICKNAME, userName);
        startActivity(intent);
    }

    private void attachUserDatabaseReferenceListener() {
        presenter.listenerDBUsers(userArrayList, userAdapter);
    }
}
