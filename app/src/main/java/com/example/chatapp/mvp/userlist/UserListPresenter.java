package com.example.chatapp.mvp.userlist;

import com.example.chatapp.adapter.UserAdapter;
import com.example.chatapp.common.User;

import java.util.ArrayList;

public class UserListPresenter {

    public UserListView view;
    private final UserListModel model;

    public UserListPresenter(UserListModel model) {
        this.model = model;
    }

    public void attachView(UserListView view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void initDB() {
        model.initDB();
    }

    public void signOutDB() {
        model.signOutDB();
    }

    public void listenerDBUsers(ArrayList<User> userArrayList, UserAdapter userAdapter) {
        model.listenerDBUsers(userArrayList, userAdapter);
    }
}
