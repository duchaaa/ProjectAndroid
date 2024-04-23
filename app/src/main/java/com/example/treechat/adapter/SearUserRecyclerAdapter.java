package com.example.treechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treechat.ChatActivity;
import com.example.treechat.R;
import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.AndroidUtil;
import com.example.treechat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearUserRecyclerAdapter extends FirestoreRecyclerAdapter<Usermodel, SearUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    public SearUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Usermodel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder userModelViewHolder, int i, @NonNull Usermodel usermodel) {
        userModelViewHolder.usernameText.setText(usermodel.getUsername());
        userModelViewHolder.phoneText.setText(usermodel.getPhone());
        if (usermodel.getUserId().equals(FirebaseUtil.currentUserID())){
            userModelViewHolder.usernameText.setText(usermodel.getUsername()+" (Bạn)");

        }

        FirebaseUtil.getOtherProfilePicStorageRef(usermodel.getUserId()).getDownloadUrl().addOnCompleteListener(ta -> {
            if (ta.isSuccessful()){
                Uri uri = ta.getResult();
                AndroidUtil.setProfilepic(context,uri,userModelViewHolder.profilePic);
            }
        });
        userModelViewHolder.itemView.setOnClickListener(v -> {
            //Điều hướng đến hoạt động chat
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,usermodel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sear_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText,phoneText;
        ImageView profilePic;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_imageView);


        }
    }
}
