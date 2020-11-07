package com.lysofts.luku.models;

import java.util.Map;

public class Chat {
    UserProfile profile;
    Map<String, Message> messages;
    Message lastMessage;

    public Chat() {
    }

    public Chat(UserProfile profile) {
        this.profile = profile;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public Map<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Message> messages) {
        this.messages = messages;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
