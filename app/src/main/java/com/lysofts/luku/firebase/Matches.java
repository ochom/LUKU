package com.lysofts.luku.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.models.NotificationModel;
import com.lysofts.luku.models.SwipeModel;
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
        db.child("users").child(userProfile.getId()).child("matches").child(myProfile.getId()).setValue(type);
        db.child("users").child(myProfile.getId()).child("matches").child(userProfile.getId()).setValue(type);


        if(type.equals("like")){
            //Send match request
            db.child("matches").push().updateChildren(data);

            //create chat profile
            new Chats().createContacts(myProfile, userProfile);

            //send notification
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.setTitle("New Love");
            notificationModel.setContent("Someone has just sent you a match request. Click to find out who they are.");
            notificationModel.setReceiver(userProfile.getId());
            db.child("notifications/matches").push().setValue(notificationModel);
        }
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
                        //new Chats().createContacts(sender, receiver);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTitle("Congratulations");
        notificationModel.setContent("We have found a match for you. Chat With them Here.");
        notificationModel.setReceiver(senderId);
        db.child("notifications/matches").push().setValue(notificationModel);
    }

    public static  void sendNewUsers(String myId, final String mySex, final String myInterest){
        final DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserProfile user = dataSnapshot.getValue(UserProfile.class);
                    String userSex, userInterest;
                    userSex = user.getSex();
                    userInterest = user.getInterestedIn();
                    if (mySex.equals("man") && myInterest.equals("men") && userSex.equals("man") && userInterest.equals("men")){
                        createNewUserNotification(user.getId());
                    }else if (mySex.equals("man") && myInterest.equals("women") && userSex.equals("woman") && userInterest.equals("men")){
                        createNewUserNotification(user.getId());
                    }else if (mySex.equals("woman") && myInterest.equals("women") && userSex.equals("woman") && userInterest.equals("women")){
                        createNewUserNotification(user.getId());
                    }else if (mySex.equals("woman") && myInterest.equals("men") && userSex.equals("man") && userInterest.equals("women")){
                        createNewUserNotification(user.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void createNewUserNotification(String id) {
        final DatabaseReference db =  FirebaseDatabase.getInstance().getReference();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTitle("New user");
        notificationModel.setContent("Hello there, A new user who you may be interested in has just sign up.");
        notificationModel.setReceiver(id);
        db.child("notifications/new_people").push().setValue(notificationModel);
    }
}
