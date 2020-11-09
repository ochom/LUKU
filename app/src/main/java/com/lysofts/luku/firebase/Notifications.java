package com.lysofts.luku.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lysofts.luku.models.NotificationModel;

public class Notifications {
    DatabaseReference db;

    public Notifications() {
        this.db = FirebaseDatabase.getInstance().getReference();
    }

    private void sendNotification(NotificationModel notificationModel){
        db.child("notifications/message").push().setValue(notificationModel);
    }
}
