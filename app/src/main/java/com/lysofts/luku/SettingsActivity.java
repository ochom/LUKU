package com.lysofts.luku;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.lysofts.luku.local.MyProfile;

public class SettingsActivity extends AppCompatActivity {
    Button btnSignOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                new MyProfile(SettingsActivity.this).deleteProfile();
                finishAffinity();
            }
        });
    }
}
