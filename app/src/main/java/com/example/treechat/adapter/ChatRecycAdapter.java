package com.example.treechat.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.treechat.R;
import com.example.treechat.model.ChatMessModel;
import com.example.treechat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecycAdapter extends FirestoreRecyclerAdapter<ChatMessModel, ChatRecycAdapter.ChatModelViewHolder> {

    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ChatRecycAdapter(@NonNull FirestoreRecyclerOptions<ChatMessModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int i, @NonNull ChatMessModel chatMessModel) {
        if (chatMessModel.getSenderId().equals(FirebaseUtil.currentUserID())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayot.setVisibility(View.VISIBLE);
            holder.rightChatTexView.setText(chatMessModel.getMessage());
        } else {
            holder.rightChatLayot.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTexView.setText(chatMessModel.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_mess_recycview_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        LinearLayout leftChatLayout, rightChatLayot;
        TextView leftChatTexView, rightChatTexView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayot = itemView.findViewById(R.id.right_chat_layout);
            leftChatTexView = itemView.findViewById(R.id.left_chat_txtview);
            rightChatTexView = itemView.findViewById(R.id.right_chat_txtview);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem deleteItem = menu.add(Menu.NONE, 1, 1, "Xóa tin nhắn");
            deleteItem.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(position);
                    return true;
                }
            }
            return false;
        }
    }
}
