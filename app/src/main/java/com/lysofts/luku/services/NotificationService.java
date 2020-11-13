package com.lysofts.luku.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.MainActivity;
import com.lysofts.luku.R;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.NotificationModel;
import com.lysofts.luku.models.UserProfile;

import java.net.URL;
import java.util.Random;

public class NotificationService extends Service {
    DatabaseReference db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = FirebaseDatabase.getInstance().getReference();
        getNewMessageNotifications();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getNewMessageNotifications() {
        db.child("notifications/chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    NotificationModel model = dataSnapshot.getValue(NotificationModel.class);
                    if (model.getReceiver().equals(FirebaseAuth.getInstance().getUid())){
                        createNotification(model, "chats", 440044);
                        snapshot.getRef().setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.child("notifications/matches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    NotificationModel model = dataSnapshot.getValue(NotificationModel.class);
                    if (model.getReceiver().equals(FirebaseAuth.getInstance().getUid())){
                        createNotification(model, "matches", 440000);
                        snapshot.getRef().setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        db.child("notifications/new_people").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    NotificationModel model = dataSnapshot.getValue(NotificationModel.class);
                    if (model.getReceiver().equals(FirebaseAuth.getInstance().getUid())){
                        createNotification(model, "home",440022);
                        snapshot.getRef().setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotification(NotificationModel model, String fragment, int notificationId) {
        UserProfile myProfile = new MyProfile(this).getProfile();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", fragment);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, model.getReceiver())
                .setSmallIcon(R.drawable.ic_baseline_favorite_received_24)
                .setContentTitle(model.getTitle())
                .setContentText(model.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "chats";
            String description = "Chats notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(model.getReceiver(), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

    }

    private int createID(){
        int randomNum = (int) (Math.random() * (Integer.MAX_VALUE - 100));
        return randomNum;
    }
}
