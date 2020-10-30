package com.lysofts.luku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
    }

    public void closeProfile(View view) {
        startActivity(new Intent(UserProfile.this,MainActivity.class));
        finish();
    }
}
