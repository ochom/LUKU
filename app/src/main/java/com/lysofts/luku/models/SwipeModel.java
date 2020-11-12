package com.lysofts.luku.models;

import java.util.List;

public class SwipeModel {
    UserProfile user;
    List<Upload> uploads;

    public SwipeModel(UserProfile user, List<Upload> uploads) {
        this.user = user;
        this.uploads = uploads;
    }

    public UserProfile getUser() {
        return user;
    }

    public List<Upload> getUploads() {
        return uploads;
    }
}
