package com.example.treechat.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class ChatroomModel {
    String chatroomId;
    List<String> userIds;
    Timestamp lastMessageTime;
    String lastMessageSendId;
    String lastMess;

    public ChatroomModel() {
        
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTime, String lastMessageSendId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSendId = lastMessageSendId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessageSendId() {
        return lastMessageSendId;
    }

    public void setLastMessageSendId(String lastMessageSendId) {
        this.lastMessageSendId = lastMessageSendId;
    }

    public String getLastMess() {
        return lastMess;
    }

    public void setLastMess(String lastMess) {
        this.lastMess = lastMess;
    }
}
