package com.lysofts.luku.models;

import java.io.Serializable;
import java.util.Map;

public class Match implements Serializable {
    UserProfile sender;
    UserProfile receiver;
    String status, type;

    public Match() {
    }

    public UserProfile getSender() {
        return sender;
    }

    public void setSender(UserProfile sender) {
        this.sender = sender;
    }

    public UserProfile getReceiver() {
        return receiver;
    }

    public void setReceiver(UserProfile receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
