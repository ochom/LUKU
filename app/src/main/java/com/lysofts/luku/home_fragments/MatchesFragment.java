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
import com.lysofts.luku.models.Match;
import com.lysofts.luku.adapters.MatchesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment   extends Fragment {
    FirebaseAuth mAth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView tvLoading;

    MatchesAdapter adapter;

    List<Match> matches;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches,  container, false);
        recyclerView = v.findViewById(R.id.matches_list);
        tvLoading = v.findViewById(R.id.tvLoading);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.child("matches").orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matches = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Match match = dataSnapshot.getValue(Match.class);
                    if (match.getType().equals("like")){
                        matches.add(match);
                    }
                }
                adapter = new MatchesAdapter(getActivity(), matches);
                recyclerView.setAdapter(adapter);
                if (matches.size()==0){
                    tvLoading.setVisibility(View.VISIBLE);
                    tvLoading.setText("No chats found.");
                }else{
                    tvLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
