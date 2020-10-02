package com.example.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.common.User;
import com.example.chatapp.mvp.userlist.UserListModel;
import com.example.chatapp.mvp.userlist.UserListView;

import java.util.ArrayList;

public class UserAdapter
        extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> userArrayList;
    private OnUserClickListener listener;
    private UserListView view;

    // оставляем interface для того чтобы потом его реализовать в UserListActivity
    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public UserAdapter(ArrayList<User> userArrayList , UserListView view) {
        this.userArrayList = userArrayList;
        this.view=view;
    }


    public void setOnClickListener(OnUserClickListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item , parent , false);

        UserViewHolder userViewHolder = new UserViewHolder(view , listener);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User currentUser = userArrayList.get(position);
        setAvatarUser(currentUser.getAvatarUri() , holder);
        holder.imageViewAvatarUser.setImageResource(currentUser.getAvatarMockUpResource());
        holder.textViewUserName.setText(currentUser.getName());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewAvatarUser;
        public TextView textViewUserName;

        public UserViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);

            imageViewAvatarUser = itemView.findViewById(R.id.imageViewAvatarUser);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();// get position
                        if(position != RecyclerView.NO_POSITION){
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }

    private void setAvatarUser(String imageUri,UserAdapter.UserViewHolder holder){
        if(!imageUri.equals("")){
            String[] splitUri = imageUri.split("/");
            String nameImage = splitUri[splitUri.length-1];
            downloadImage(nameImage , holder);
        }
    }

    private void downloadImage(String nameImage ,UserAdapter.UserViewHolder holder){
        UserListModel model = new UserListModel();
        model.setHolder(holder);
        model.setView(view);
        model.downloadImage(nameImage);
    }


}
