package com.example.treechat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treechat.adapter.ChatRecycAdapter;
import com.example.treechat.adapter.SearUserRecyclerAdapter;
import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchBnt,backBtn;
    RecyclerView recyclerView;
    SearUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchInput = findViewById(R.id.seach_user_input);
        searchBnt = findViewById(R.id.seach_user_btn);
        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recyclerView);

        searchInput.requestFocus();

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        searchBnt.setOnClickListener(v -> {
            String searTerm = searchInput.getText().toString();
            if(searTerm.isEmpty() || searTerm.length()<2){
                searchInput.setError("Tên tối thiểu 2 ký tự");
                return;
            }
            setupSearRecyclerView(searTerm);
        });
    }
    void setupSearRecyclerView(String searTerm){

        Query query = FirebaseUtil.allUsercollection()
                .whereGreaterThanOrEqualTo("username",searTerm)
                .whereLessThanOrEqualTo("username",searTerm+ "utf8");

        FirestoreRecyclerOptions<Usermodel> options = new FirestoreRecyclerOptions.Builder<Usermodel>()
                .setQuery(query,Usermodel.class).build();

        adapter = new SearUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.startListening();
        }
    }
}