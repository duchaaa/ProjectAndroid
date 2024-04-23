package com.example.treechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treechat.ChatActivity;
import com.example.treechat.R;
import com.example.treechat.model.ChatroomModel;
import com.example.treechat.model.Usermodel;
import com.example.treechat.utils.AndroidUtil;
import com.example.treechat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    private Context context;
    private OnDeleteItemClickListener onDeleteItemClickListener;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.onDeleteItemClickListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessByMe = model.getLastMessageSendId().equals(FirebaseUtil.currentUserID());
                        Usermodel otherUsermodel = task.getResult().toObject(Usermodel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUsermodel.getUserId()).getDownloadUrl().addOnCompleteListener(ta -> {
                            if (ta.isSuccessful()) {
                                Uri uri = ta.getResult();
                                AndroidUtil.setProfilepic(context, uri, holder.profilePic);
                            }
                        });

                        holder.userText.setText(otherUsermodel.getUsername());
                        if (lastMessByMe)
                            holder.lastMessText.setText("You: " + model.getLastMess());
                        else
                            holder.lastMessText.setText(model.getLastMess());
                        holder.lastMessTime.setText(FirebaseUtil.timestampTostring(model.getLastMessageTime()));

                        holder.itemView.setOnClickListener(v -> {
                            // Navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUsermodel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                        // Assign delete action to itemView
                        holder.itemView.setOnLongClickListener(v -> {
                            if (onDeleteItemClickListener != null) {
                                onDeleteItemClickListener.onDeleteItemClick(position);
                                return true;
                            }
                            return false;
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resen_chat_recyc_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        TextView lastMessTime;
        TextView lastMessText;
        TextView userText;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userText = itemView.findViewById(R.id.user_name_text);
            lastMessTime = itemView.findViewById(R.id.last_mess_time_text);
            lastMessText = itemView.findViewById(R.id.last_mess_text);
            profilePic = itemView.findViewById(R.id.profile_pic_imageView);

            // Register ContextMenu for itemView
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem deleteItem = menu.add(Menu.NONE, R.id.item_Delete, Menu.NONE, "Xóa tin nhắn");
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            if (onDeleteItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteItemClickListener.onDeleteItemClick(position);
                    return true;
                }
            }
            return false;
        }
    }

    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);
    }
}
