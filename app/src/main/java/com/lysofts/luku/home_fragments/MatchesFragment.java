package com.lysofts.luku.home_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.lysofts.luku.view_holders.MatchesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment   extends Fragment {
    FirebaseAuth mAth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    MatchesAdapter adapter;

    List<Match> matches;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches,  container, false);
        recyclerView = v.findViewById(R.id.matches_list);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference.child("matches").addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
