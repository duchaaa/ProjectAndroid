package com.example.treechat;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.treechat.adapter.ChatRecycAdapter;
import com.example.treechat.model.ChatMessModel;
import com.example.treechat.model.ChatroomModel;
import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.AndroidUtil;
import com.example.treechat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    Usermodel otherUser;
    EditText messInput;
    ImageButton sendMess, backBtn;
    TextView otherUserName;
    RecyclerView recyclerView;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecycAdapter adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otherUser = AndroidUtil.getUserModelFrom(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserID(), otherUser.getUserId());

        messInput = findViewById(R.id.chat_mess_input);
        sendMess = findViewById(R.id.messsge_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUserName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recyclerView);

        imageView = findViewById(R.id.profile_pic_imageView);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(ta -> {
            if (ta.isSuccessful()){
                Uri uri = ta.getResult();
                AndroidUtil.setProfilepic(this,uri,imageView);
            }
        });

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        otherUserName.setText(otherUser.getUsername());

        sendMess.setOnClickListener((v -> {
            String messsage = messInput.getText().toString().trim();
            if(messsage.isEmpty())
                return;
            sendMessToUser(messsage);

        }));

        getOrCreateChatroomModel();
        setupChatRecycView();
    }

    void setupChatRecycView() {
        Query query = FirebaseUtil.getChatroomMessReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessModel> options = new FirestoreRecyclerOptions.Builder<ChatMessModel>()
                .setQuery(query,ChatMessModel.class).build();
        adapter = new ChatRecycAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.setOnItemClickListener(new ChatRecycAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteMessage(position);
            }

            private void deleteMessage(int position) {
                adapter.getSnapshots().getSnapshot(position).getReference().delete();
            }

        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessToUser(String message) {
        chatroomModel.setLastMessageTime(Timestamp.now());
        chatroomModel.setLastMessageSendId(FirebaseUtil.currentUserID());
        chatroomModel.setLastMess(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessModel chatMessModel = new ChatMessModel(message,FirebaseUtil.currentUserID(),Timestamp.now());
        FirebaseUtil.getChatroomMessReference(chatroomId).add(chatMessModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            messInput.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserID(), otherUser.getUserId()), Timestamp.now(), ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendNotification(String messsage) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Usermodel currenUser = task.getResult().toObject(Usermodel.class);
                try{
                    JSONObject jsonObject = new JSONObject();


                    JSONObject jsonObjectNoitifi = new JSONObject();
                    assert currenUser != null;
                    jsonObjectNoitifi.put("title",currenUser.getUsername());
                    jsonObjectNoitifi.put("body",messsage);

                    JSONObject jsonObjectData = new JSONObject();
                    jsonObjectData.put("userId",currenUser.getUserId());

                    jsonObject.put("notification",jsonObjectNoitifi);
                    jsonObject.put("data",jsonObjectData);
                    jsonObject.put("to",otherUser.getFCMtoken());

                    callApi(jsonObject);
                }catch (Exception e){

                }
            }
        });
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAeRxRpFY:APA91bG-f6CAhNnx3mFDPkJiM0_xC6yLXwMZNujszWnMA_CXMucPhYNP-9NhFeYG00CyGZLB4-n9g7TTQmNdKftuExhzSnnKojh2C1kIZzgcYtvWCeiQkXiKrJJKW0qKAZ3cm5HsAtSh")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

        });
    }
}

