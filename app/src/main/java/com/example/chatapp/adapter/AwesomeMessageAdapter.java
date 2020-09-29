package com.example.chatapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.common.AwesomeMessage;

import java.util.List;

public class AwesomeMessageAdapter extends ArrayAdapter<AwesomeMessage> {

    private List<AwesomeMessage> messages;
    private Activity activity;

    public AwesomeMessageAdapter(@NonNull Activity context, int resource,
                                 List<AwesomeMessage> messages) {
        super(context, resource, messages);
        this.messages = messages;
        this.activity = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater)
                activity.getLayoutInflater();

        AwesomeMessage message = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            layoutResource = R.layout.my_message_item;
        } else {
            layoutResource = R.layout.your_message_item;
        }

        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder.textViewMessage.setText(message.getText());
        viewHolder.imageViewPhoto.setVisibility(View.GONE);
        if (message.getImageUrl() != null && message.getIsImage()) {
            viewHolder.imageViewPhoto.setVisibility(View.VISIBLE);
            downloadImage(message, viewHolder);
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        // переопределяем метод для обработки двух случаев
        int flag;
        AwesomeMessage awesomeMessage = messages.get(position);
        if (awesomeMessage.isMineMessage()) {
            flag = 0;

        } else {
            flag = 1;
        }

        return flag;
    }

    @Override
    public int getViewTypeCount() {//возвращение кол-ва вариаций
        return 2;
    }

    private static class ViewHolder {
        private TextView textViewMessage;
        private ImageView imageViewPhoto;

        public ViewHolder(View view) {
            textViewMessage = view.findViewById(R.id.textViewMessage);
            imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        }
    }


    private void downloadImage(AwesomeMessage message, ViewHolder holder) {
        /*
        Загрузка изображения с помощью
        библиотеки Glide
         */
        Glide.with(holder.imageViewPhoto.getContext())
                .load(message.getImageUrl())
                .into(holder.imageViewPhoto);
    }
}
