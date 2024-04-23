package com.example.treechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.AndroidUtil;
import com.example.treechat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent().getExtras() != null){
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allChatroomCollectionRefer().document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    //Điều hướng đến hoạt động chat
                    Usermodel model = task.getResult().toObject(Usermodel.class);

                    Intent intentmain = new Intent(this,MainActivity.class);
                    intentmain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentmain);

                    Intent intent = new Intent(this, ChatActivity.class);
                    AndroidUtil.passUserModelAsIntent(intent,model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this,LoginNumberActivity.class));
                    }
                    finish();
                }
            },1000);
        }
    }
}