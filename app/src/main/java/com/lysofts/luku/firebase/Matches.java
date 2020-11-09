package com.lysofts.luku.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.models.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Matches {

    public static void sendMatchRequest(UserProfile myProfile, UserProfile userProfile, String type){
        DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = simpleDF.format(new Date());
        Map<String, Object> data = new HashMap<>();
        data.put("sender", myProfile);
        data.put("receiver", userProfile);
        data.put("status", "pending");
        data.put("type", type);
        data.put("time", time);
        db.child("matches").push().updateChildren(data);
    }

    public static void confirmMatch(final String senderId, final String receiverId) {
        final DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        db.child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserProfile sender = dataSnapshot.child("sender").getValue(UserProfile.class);
                    UserProfile receiver = dataSnapshot.child("receiver").getValue(UserProfile.class);
                    if (sender.getId().equals(senderId) && receiver.getId().equals(receiverId)){
                        dataSnapshot.getRef().child("status").setValue("matched");

                        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
                        String time = simpleDF.format(new Date());
                        dataSnapshot.getRef().child("time").setValue(time);
                        new Chats().createContacts(sender, receiver);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
