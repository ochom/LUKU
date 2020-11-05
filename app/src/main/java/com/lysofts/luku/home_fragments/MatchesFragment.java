package com.lysofts.luku.home_fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lysofts.luku.R;
import com.lysofts.luku.firebase.Matches;
import com.lysofts.luku.models.Match;
import com.lysofts.luku.view_holders.MatchViewHolder;

public class MatchesFragment   extends Fragment {
    FirebaseAuth mAth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

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

        Query query = databaseReference.child("users").child(mAth.getUid()).child("matches");
        FirebaseRecyclerOptions<Match>  options= new FirebaseRecyclerOptions
                .Builder<Match>()
                .setQuery(query, Match.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Match, MatchViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onBindViewHolder(@NonNull MatchViewHolder holder, int position, @NonNull final Match model) {
                holder.setMatch(model);
                holder.imgAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Matches(getActivity()).confirmMatch(model.getId());
                    }
                });
            }

            @NonNull
            @Override
            public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.matches_design, parent, false);
                return new MatchViewHolder(v);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("::LUKU", "Adapter listening...");
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }
}
