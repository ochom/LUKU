package com.lysofts.luku;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.chat_app.ChatActivity;

public class Splash extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    int STORAGE_PERM = 0, LOCATION_PERM=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        checkPermissions();
    }

    private void checkPermissions() {
        ActivityCompat.requestPermissions(Splash.this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && allGranted(grantResults)){
                    initializeAuth();
                }else{
                    finish();
                }
                break;
            default:
                finish();
                break;
        }
    }

    private boolean allGranted(int[] grantResults){
        for(int i:grantResults){
            if (i == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    private void initializeAuth() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            checkProfile(mAuth.getCurrentUser());
        }else{
            Intent intent = new Intent(Splash.this, SignUp.class);
            intent.putExtra("state","register_user");
            startActivity(intent);
            finish();
        }
    }


    private void checkProfile(final FirebaseUser currentUser){
        databaseReference.child("users").child(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(currentUser.getUid())){
                    startActivity(new Intent(Splash.this, MainActivity.class));
                }else{
                    Intent intent = new Intent(Splash.this, SignUp.class);
                    intent.putExtra("state","create_profile");
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}