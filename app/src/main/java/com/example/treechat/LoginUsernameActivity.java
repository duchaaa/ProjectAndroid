package com.example.treechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button letmeBtn;
    ProgressBar progressBar;
    String phoneNumber;
    Usermodel usermodel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_username);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameInput = findViewById(R.id.login_username);
        letmeBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        letmeBtn.setOnClickListener(v -> {
            setUsername();
        });

    }

    void setUsername(){

        String userNa = usernameInput.getText().toString();
        if (userNa.isEmpty() || userNa.length()<2){
            usernameInput.setError("Tên tối thiểu 2 ký tự");
            return;
        }
        setInProgress(true);
        if (usermodel!=null){
            usermodel.setUsername(userNa);
        }else{
            usermodel = new Usermodel(phoneNumber,userNa, Timestamp.now(),FirebaseUtil.currentUserID());
        }

        FirebaseUtil.currentUserDetails().set(usermodel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginUsernameActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                  usermodel =  task.getResult().toObject(Usermodel.class);
                  if (usermodel!=null){
                      usernameInput.setText(usermodel.getUsername());
                  }
                }
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letmeBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            letmeBtn.setVisibility(View.VISIBLE);
        }
    }
}