package com.example.chatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.model.AwesomeMessage;

import java.util.List;

public class AwesomeMessageAdapter extends ArrayAdapter<AwesomeMessage> {

    private boolean isText;
    private ImageView imageViewPhoto;
    private TextView textViewText;
    private TextView textViewName;

    public AwesomeMessageAdapter(@NonNull Context context, int resource,
                                 List<AwesomeMessage> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            // create view message if view  not created
            view = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.message_item, parent, false);
        }
        findViews(view);

        // получаем класс добавленный в адаптер методом adapter.add(new AwesomeMessage())
        AwesomeMessage awesomeMessage = getItem(position);
        isText = awesomeMessage.getImageUrl() == null;
        inflateMessage(awesomeMessage);
        downloadImage(awesomeMessage);
        return view;
    }

    private void inflateMessage(AwesomeMessage message){
        if (isText) {
            //textViewText.setVisibility(View.VISIBLE);
            textViewText.setText(message.getText());
            imageViewPhoto.setVisibility(View.GONE);
        } else {
            //textViewText.setVisibility(View.GONE);
            textViewText.setText(message.getText());
            imageViewPhoto.setVisibility(View.VISIBLE);
        }

        textViewName.setText(message.getName());
    }

    private void findViews(View view){
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        textViewText = view.findViewById(R.id.textViewText);
        textViewName = view.findViewById(R.id.textViewName);
    }

    private void downloadImage(AwesomeMessage message){
        /*
        Загрузка изображения с помощью
        библиотеки Glide
         */
        Glide.with(imageViewPhoto.getContext())
                .load(message.getImageUrl())
                .into(imageViewPhoto);
    }
}
