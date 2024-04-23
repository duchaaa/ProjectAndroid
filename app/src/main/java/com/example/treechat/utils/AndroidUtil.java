package com.example.treechat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.treechat.model.Usermodel;

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent , Usermodel usermodel){
            intent.putExtra("username", usermodel.getUsername());
            intent.putExtra("phone", usermodel.getPhone());
            intent.putExtra("userIds", usermodel.getUserId());
            intent.putExtra("fcmToken", usermodel.getFCMtoken());
        }


    public static Usermodel getUserModelFrom(Intent intent){
        Usermodel usermodel = new Usermodel();
            usermodel.setUsername(intent.getStringExtra("username"));
            usermodel.setPhone(intent.getStringExtra("phone"));
            usermodel.setUserId(intent.getStringExtra("userIds"));
            usermodel.setFCMtoken(intent.getStringExtra("fcmToken"));
        return usermodel;
    }

    public static void setProfilepic(Context context, Uri imageuri, ImageView imageView){
        Glide.with(context).load(imageuri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
