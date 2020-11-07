package com.lysofts.luku.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.models.Message;
import com.lysofts.luku.models.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chats {
    public static void createContacts(UserProfile myProfile, UserProfile otherUser){
        DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        db.child("chats").child(myProfile.getId()).child(otherUser.getId()).child("profile").setValue(otherUser);
        db.child("chats").child(otherUser.getId()).child(myProfile.getId()).child("profile").setValue(myProfile);
    }

    public static void sendMessage(String myId, String userId, String text) {
        DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat simpleDF = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        String time = simpleDF.format(new Date());
        Message message = new Message();
        message.setText(text);
        message.setTime(time);
        message.setSender(myId);
        message.setStatus("pending");

        db.child("chats").child(myId).child(userId).child("messages").push().setValue(message);
        db.child("chats").child(myId).child(userId).child("lastMessage").setValue(message);

        db.child("chats").child(userId).child(myId).child("messages").push().setValue(message);
        db.child("chats").child(userId).child(myId).child("lastMessage").setValue(message);
    }

    public static void markMessagesAsRead(final String myId, final String userId) {
        final DatabaseReference db =  FirebaseDatabase.getInstance().getReference();

        db.child("chats").child(userId).child(myId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> messageKeys = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    if (!message.getSender().equals(myId)){
                        messageKeys.add(dataSnapshot.getKey());
                    }
                db.removeEventListener(this);
                markAsRead(myId, userId, messageKeys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.child("chats").child(userId).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("lastMessage")){
                    Message message = snapshot.child("lastMessage").getValue(Message.class);
                    db.removeEventListener(this);
                    if (!message.getSender().equals(myId)){
                        db.child("chats").child(userId).child(myId).child("lastMessage").child("status").setValue("seen");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void markAsRead(String myId, String userId, List<String> messageKeys) {
        final DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        for (String key: messageKeys){
            db.child("chats").child(userId).child(myId).child("messages").child(key).child("status").setValue("seen");
        }
    }
}
