package com.lysofts.luku.home_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.R;
import com.lysofts.luku.models.Chat;
import com.lysofts.luku.adapters.ChatsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment  extends Fragment {
    FirebaseAuth mAth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView tvLoading;

    ChatsAdapter adapter;

    List<Chat> chats;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats,  container, false);
        recyclerView = v.findViewById(R.id.chats_list);
        tvLoading = v.findViewById(R.id.tvLoading);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.child("chats")
                .child(mAth.getUid())
                .orderByChild("lastMessage/time")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    chats.add(chat);
                }
                adapter = new ChatsAdapter(getActivity(), chats);
                recyclerView.setAdapter(adapter);
                if (chats.size()==0){
                    tvLoading.setVisibility(View.VISIBLE);
                    tvLoading.setText("No chats found.");
                }else{
                    tvLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvLoading.setVisibility(View.VISIBLE);
                tvLoading.setText("Error occurred while loading chats.");
            }
        });
    }
}
