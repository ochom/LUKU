package com.lysofts.luku;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.R;
import com.lysofts.luku.adapters.MessagesAdapter;
import com.lysofts.luku.firebase.Chats;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.Match;
import com.lysofts.luku.models.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    String userId, name, userImage, title;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    Map<DatabaseReference, ValueEventListener> databaseListeners;

    ImageView imgProfile;
    TextView tvName, tvTitle;
    List<Message> messages;


    EditText txtMessage;
    ImageView btnSend;

    RecyclerView recyclerView;
    MessagesAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_chat);
        mAuth = FirebaseAuth.getInstance();
        databaseReference  = FirebaseDatabase.getInstance().getReference();

        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);

        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(sendMessage);

        userId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("name");
        userImage = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        updateUI();
        textWatcher();

        recyclerView = findViewById(R.id.messages_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference.child("chats/"+mAuth.getUid()+"/"+userId+"/messages")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                adapter = new MessagesAdapter(ChatActivity.this, messages, userImage);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(messages.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        new Chats().sendMessage(mAuth.getUid(), userId, txtMessage.getText().toString(),new MyProfile(ChatActivity.this).getProfile().getName());
        txtMessage.setText("");
        }
    };

    private void updateUI() {
        Picasso.get().load(userImage).placeholder(R.drawable.ic_baseline_account_circle_24).into(imgProfile);
        tvName.setText(name);
        tvTitle.setText(title);
    }

    private void textWatcher() {
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(txtMessage.getText())){
                    btnSend.setImageResource(R.drawable.ic_baseline_send_active );
                    btnSend.setEnabled(true);
                }else{
                    btnSend.setImageResource(R.drawable.ic_baseline_send_24);
                    btnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseListeners = new Chats().markMessagesAsRead(mAuth.getUid(), userId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : databaseListeners.entrySet()) {
            entry.getKey().removeEventListener(entry.getValue());
        }
    }

    public void closeActivity(View view) {
        finish();
    }
}
