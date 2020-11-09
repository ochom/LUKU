package com.lysofts.luku.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.models.Message;
import com.lysofts.luku.models.NotificationModel;
import com.lysofts.luku.models.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chats {
    DatabaseReference db;

    public  Chats(){
        db = FirebaseDatabase.getInstance().getReference();
    }

    public  void createContacts(UserProfile myProfile, UserProfile otherUser){
        db =  FirebaseDatabase.getInstance().getReference();
        db.child("chats").child(myProfile.getId()).child(otherUser.getId()).child("profile").setValue(otherUser);
        db.child("chats").child(otherUser.getId()).child(myProfile.getId()).child("profile").setValue(myProfile);
    }

    public  void sendMessage(String myId, String userId, String text) {
        db =  FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = simpleDF.format(new Date());
        Message message = new Message();
        message.setText(text);
        message.setTime(time);
        message.setSender(myId);
        message.setRead(false);

        db.child("chats").child(myId).child(userId).child("messages").push().setValue(message);
        db.child("chats").child(myId).child(userId).child("lastMessage").setValue(message);

        db.child("chats").child(userId).child(myId).child("messages").push().setValue(message);
        db.child("chats").child(userId).child(myId).child("lastMessage").setValue(message);

        //send notification
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTitle("New Message");
        notificationModel.setContent(text);
        notificationModel.setReceiver(userId);
        db.child("notifications").push().setValue(notificationModel);
    }

    public  Map<DatabaseReference, ValueEventListener> markMessagesAsRead(final String myId, final String userId) {
        Map<DatabaseReference, ValueEventListener> databaseListeners = new HashMap<>();
        databaseListeners.put(db.child("chats").child(myId).child(userId).child("messages"), myDBReceivedListener(myId));
        databaseListeners.put(db.child("chats").child(userId).child(myId).child("messages"), userDBSentListener(myId));
        databaseListeners.put( db.child("chats").child(userId).child(myId), userDBLastMessageListener(myId));

        for (Map.Entry<DatabaseReference, ValueEventListener> entry: databaseListeners.entrySet()){
            entry.getKey().addValueEventListener(entry.getValue());
        }
        return databaseListeners;
    }

    private ValueEventListener myDBReceivedListener(final String myId) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    if (!message.getSender().equals(myId)){
                        dataSnapshot.child("read").getRef().setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener userDBSentListener(final String myId) {
        return  new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    if (!message.getSender().equals(myId)){
                        dataSnapshot.child("read").getRef().setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener userDBLastMessageListener(final String myId) {
        return  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("lastMessage")){
                    Message message = snapshot.child("lastMessage").getValue(Message.class);
                    if (!message.getSender().equals(myId)){
                        snapshot.child("lastMessage").child("read").getRef().setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
}
