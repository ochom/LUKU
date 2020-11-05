package com.lysofts.luku.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.lysofts.luku.models.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class MyProfile {
    UserProfile profile;
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    public  MyProfile(Context context){
        this.sharedPreferences = context.getSharedPreferences("local", Context.MODE_PRIVATE);
    }

    public void setProfile(UserProfile userProfile){
        Map<String, Object> data = new HashMap<>();
        data.put("id", userProfile.getId());
        data.put("name", userProfile.getName());
        data.put("title", userProfile.getTitle());
        data.put("profession", userProfile.getProfession());
        data.put("email", userProfile.getEmail());
        data.put("sex", userProfile.getSex());
        data.put("interestedIn", userProfile.getInterestedIn());
        data.put("birthday", userProfile.getBirthday());
        data.put("phone", userProfile.getPhone());
        data.put("image", userProfile.getImage());

        sharedPreferences.edit().putString("MyProfile", gson.toJson(data)).commit();
    }

    public UserProfile getProfile(){
        String sharedData = sharedPreferences.getString("MyProfile",null);
        if (sharedData != null){
            profile = gson.fromJson(sharedData, UserProfile.class);
        }
        return profile;
    }

    public void deleteProfile() {
        sharedPreferences.edit().clear().commit();
    }
}
