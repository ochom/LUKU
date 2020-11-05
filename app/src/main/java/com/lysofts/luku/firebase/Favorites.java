package com.lysofts.luku.firebase;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class Favorites {
    DatabaseReference db ;
    FirebaseUser userAuth;
    Context context;
    public  Favorites(Context ctx){
        this.context = ctx;
        this.db = FirebaseDatabase.getInstance().getReference();
        this.userAuth = FirebaseAuth.getInstance().getCurrentUser();
    }

    public  void addFavorite(UserProfile user){
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("image", user.getImage());
        data.put("title", user.getTitle());
        db.child("users").child(userAuth.getUid()).child("Favorites").child(user.getId()).updateChildren(data);

        Map<String, Object> data2 = new HashMap<>();
        UserProfile userProfile = new MyProfile(context).getProfile();
        data2.put("id", userProfile.getId());
        data2.put("name", userProfile.getName());
        data2.put("image", userProfile.getImage());
        data2.put("title", userProfile.getTitle());
        db.child("users").child(user.getId()).child("matchesReceived").child(userProfile.getId()).updateChildren(data2);
    }
}
