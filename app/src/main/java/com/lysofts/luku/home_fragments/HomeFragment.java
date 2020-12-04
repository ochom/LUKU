package com.lysofts.luku.home_fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.R;
import com.lysofts.luku.adapters.SwipeDeckAdapter;
import com.lysofts.luku.firebase.Matches;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.SwipeModel;
import com.lysofts.luku.models.Upload;
import com.lysofts.luku.models.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment  extends Fragment {
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    TextView tvLoading;
    ImageButton btnLike, btnDislike;
    ImageView image_blank_page;
    RelativeLayout noDataView;

    SwipeDeck swipeDeck;

    UserProfile myProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        swipeDeck = v.findViewById(R.id.swipe_deck);

        tvLoading = v.findViewById(R.id.tvLoading);
        noDataView = v.findViewById(R.id.noDataView);
        image_blank_page = v.findViewById(R.id.image_blank_page);
        btnLike = v.findViewById(R.id.btnLike);
        btnDislike = v.findViewById(R.id.btnDislike);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
        myProfile = new MyProfile(getActivity()).getProfile();

        btnLike.setVisibility(View.GONE);
        btnDislike.setVisibility(View.GONE);
        image_blank_page.setVisibility(View.GONE);
        getAUser();

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeDeck.swipeTopCardLeft(2000);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeDeck.swipeTopCardRight(2000);
            }
        });
    }

    private void getAUser() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                List<SwipeModel> models = new ArrayList<>();
                for(DataSnapshot snapshot1: snapshot.getChildren()){

                    if (i==30){return;}

                    UserProfile  user = snapshot1.getValue(UserProfile.class);
                    List<Upload> uploads = new ArrayList<>();
                    Map<String, String> matches = new HashMap<>();

                    if (snapshot1.child("uploads") != null){
                        for (DataSnapshot snapshot2:snapshot1.child("uploads").getChildren()){
                            Upload upload = snapshot2.getValue(Upload.class);
                            uploads.add(upload);
                        }
                    }

                    if (snapshot1.child("matches") != null){
                        for (DataSnapshot snapshot2:snapshot1.child("matches").getChildren()){
                            matches.put(snapshot2.getKey(), snapshot2.getValue(String.class));
                        }
                    }

                    assert user != null;
                    if (!user.getId().equals(mAuth.getUid()) && !matches.containsKey(mAuth.getUid())){
                        String userSex, userInterest, mySex, myInterest;
                        userSex = user.getSex();
                        userInterest = user.getInterestedIn();
                        mySex = myProfile.getSex();
                        myInterest = myProfile.getInterestedIn();


                        if (mySex.equals("man") && myInterest.equals("men") && userSex.equals("man") && userInterest.equals("men")){
                            i++;
                            models.add(new SwipeModel(user, uploads));
                            createFlipper(models);
                        }else if (mySex.equals("man") && myInterest.equals("women") && userSex.equals("woman") && userInterest.equals("men")){
                            i++;
                            models.add(new SwipeModel(user, uploads));
                            createFlipper(models);
                        }else if (mySex.equals("woman") && myInterest.equals("women") && userSex.equals("woman") && userInterest.equals("women")){
                            i++;
                            models.add(new SwipeModel(user, uploads));
                            createFlipper(models);
                        }else if (mySex.equals("woman") && myInterest.equals("men") && userSex.equals("man") && userInterest.equals("women")){
                            i++;
                            models.add(new SwipeModel(user, uploads));
                            createFlipper(models);
                        }
                    }
                }

                if (models.size()==0){
                    noDataView.setVisibility(View.VISIBLE);
                    tvLoading.setVisibility(View.GONE);
                    btnLike.setVisibility(View.GONE);
                    btnDislike.setVisibility(View.GONE);
                    image_blank_page.setVisibility(View.VISIBLE);
                }else{
                    noDataView.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    btnLike.setVisibility(View.VISIBLE);
                    btnDislike.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvLoading.setVisibility(View.VISIBLE);
                tvLoading.setText("Error occurred while loading people.");
                btnLike.setVisibility(View.GONE);
                btnDislike.setVisibility(View.GONE);
            }
        });
    }

    private void createFlipper(final List<SwipeModel> models) {
        SwipeDeckAdapter adapter = new SwipeDeckAdapter(getActivity(), models);
        swipeDeck.setAdapter(adapter);
        swipeDeck.setHardwareAccelerationEnabled(true);
        swipeDeck.setLeftImage(R.id.left_image);
        swipeDeck.setRightImage(R.id.right_image);
        swipeDeck.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                Matches.sendMatchRequest(new MyProfile(getActivity()).getProfile(), models.get(position).getUser(), "like");
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                Matches.sendMatchRequest(new MyProfile(getActivity()).getProfile(), models.get(position).getUser(), "dislike");
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                tvLoading.setVisibility(View.VISIBLE);
                //tvLoading.setText("No more people of your Taste.");
                btnLike.setVisibility(View.GONE);
                btnDislike.setVisibility(View.GONE);
            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });
    }
}
