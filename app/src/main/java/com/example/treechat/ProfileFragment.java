package com.example.treechat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.AndroidUtil;
import com.example.treechat.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    TextView logoutBtn;
    ProgressBar progressBar;
    Button updateProfileBtn;
    EditText userNameInput, phoneInput;
    ImageView profilePic;
    Usermodel currenUsermodel;
    ActivityResultLauncher<Intent> imagePicLauncher;
    Uri selectedImageUri;
    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null && data.getData() != null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilepic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        logoutBtn = view.findViewById(R.id.logout_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        userNameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        profilePic = view.findViewById(R.id.profile_image_view);
        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));
        logoutBtn.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseUtil.logout();
                        Intent intent = new Intent(getContext(),SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });

        });

        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePicLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;
    }



    void updateBtnClick(){
        String newuserNa = userNameInput.getText().toString();
        if (newuserNa.isEmpty() || newuserNa.length()<2){
            userNameInput.setError("Tên tối thiểu 2 ký tự");
            return;
        }
        currenUsermodel.setUsername(newuserNa);
        setInProgress(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri).addOnCompleteListener(task -> {
                updatetoFirestore();
            });
        }else {
            updatetoFirestore();
        }

    }

    void updatetoFirestore(){
        FirebaseUtil.currentUserDetails().set(currenUsermodel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                AndroidUtil.showToast(getContext(),"Cập nhật thành công");
            }else {
                AndroidUtil.showToast(getContext(),"Cập nhật thất bại");
            }
        });
    }
     void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Uri uri = task.getResult();
                AndroidUtil.setProfilepic(getContext(),uri,profilePic);
            }
        });
         FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
             setInProgress(false);
             currenUsermodel = task.getResult().toObject(Usermodel.class);
             userNameInput.setText(currenUsermodel.getUsername());
             phoneInput.setText(currenUsermodel.getPhone());

         });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}