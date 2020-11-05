package com.lysofts.luku.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class Matches {
    DatabaseReference db ;
    FirebaseUser userAuth;
    Context context;
    public  Matches(Context ctx){
        this.context = ctx;
        this.db = FirebaseDatabase.getInstance().getReference();
        this.userAuth = FirebaseAuth.getInstance().getCurrentUser();
    }

    public  void sendMatchRequest(UserProfile user){
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("image", user.getImage());
        data.put("title", user.getTitle());
        data.put("birthday", user.getBirthday());
        data.put("status", "sent");
        db.child("users").child(userAuth.getUid()).child("matches").child(user.getId()).updateChildren(data);

        Map<String, Object> data2 = new HashMap<>();
        UserProfile userProfile = new MyProfile(context).getProfile();

        data2.put("id", userProfile.getId());
        data2.put("name", userProfile.getName());
        data2.put("image", userProfile.getImage());
        data2.put("title", userProfile.getTitle());
        data2.put("birthday", userProfile.getBirthday());
        data2.put("status", "received");
        db.child("users").child(user.getId()).child("matches").child(userAuth.getUid()).updateChildren(data2);
    }

    public void confirmMatch(String id) {
        //update my matches
        db.child("users").child(userAuth.getUid()).child("matches").child(id).child("status").setValue("matched");

        //update the other user matches
        db.child("users").child(id).child("matches").child(userAuth.getUid()).child("status").setValue("matched");
    }
}
