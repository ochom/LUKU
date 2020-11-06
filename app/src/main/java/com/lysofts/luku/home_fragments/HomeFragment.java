package com.lysofts.luku.home_fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.R;
import com.lysofts.luku.chat_app.ChatActivity;
import com.lysofts.luku.constants.MyConstants;
import com.lysofts.luku.firebase.Matches;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.Upload;
import com.lysofts.luku.models.UserProfile;
import com.lysofts.luku.swipe.SwipeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class HomeFragment  extends Fragment {
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    List<UserProfile> users = new ArrayList<>();
    ViewFlipper viewFlipper;
    CardView cardView;

    UserProfile myProfile;

    private Animation slideLeftIn;
    private Animation slideLeftOut;


    int counter = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        viewFlipper = v.findViewById(R.id.viewFlipper);
        slideLeftIn = AnimationUtils.loadAnimation(getContext(), R.anim.stream_flipper_slidein_from_right);
        slideLeftOut = AnimationUtils.loadAnimation(getContext(), R.anim.stream_flipper_slideout_to_left);
        viewFlipper.setInAnimation(slideLeftIn);
        viewFlipper.setOutAnimation(slideLeftOut);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth =  FirebaseAuth.getInstance();
        myProfile = new MyProfile(getActivity()).getProfile();

        loadUsers();
    }

    private void loadUsers() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    UserProfile  user = snapshot1.getValue(UserProfile.class);
                    if (!user.getId().equals(mAuth.getUid())){
                        if (user.getSex().equals("man") && myProfile.getInterestedIn().equals("men")){
                            users.add(user);
                        }else if (user.getSex().equals("woman") && myProfile.getInterestedIn().equals("women")){
                            users.add(user);
                        }else if (myProfile.getInterestedIn().equals("any")){
                            users.add(user);
                        }
                    }
                }
                createFlipper();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createFlipper() {
        if(users !=null && users.size()>0 && getActivity()!=null){
            for(final UserProfile user: users){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.person_preview, null);
                cardView = view.findViewById(R.id.user_preview);
                TextView txtName = view.findViewById(R.id.txtName);
                TextView txtTitle = view.findViewById(R.id.txtTitle);
                final ProgressBar progressBar = view.findViewById(R.id.progress_circular);
                final ImageView imageView = view.findViewById(R.id.image);
                View btnPrev = view.findViewById(R.id.btnPrev);
                View btnNext = view.findViewById(R.id.btnNext);

                ImageButton btnLike = view.findViewById(R.id.btnLike);
                ImageButton btnDislike = view.findViewById(R.id.btnDislike);

                final StoriesProgressView storiesProgressView = view.findViewById(R.id.stories);

                final List<Upload> uploads = new ArrayList<>();
                if(user.getUploads()!=null && user.getUploads().size()>0){
                    for(Map.Entry<String, Upload> entry :  user.getUploads().entrySet()){
                        Upload upload = entry.getValue();
                        uploads.add(upload);
                    }
                }
                int age = MyConstants.getAge(user.getBirthday());
                txtName.setText(user.getName()+", "+age);
                txtTitle.setText(user.getTitle());
                createStories(view, uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    Matches.sendMatchRequest(new MyProfile(getActivity()).getProfile(), user, "like");
                    }
                });

                btnDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    Matches.sendMatchRequest(new MyProfile(getActivity()).getProfile(), user, "dislike");
                    }
                });
            }
        }
        handleSwipes();
    }

    private void createStories(final View view, final List<Upload> uploads, final StoriesProgressView storiesProgressView,
                               final ImageView imageView, final ProgressBar progressBar, final View btnPrev, final View btnNext) {
        if(uploads!=null && uploads.size()>0){
            viewFlipper.removeAllViews();
            counter=0;
            storiesProgressView.setStoriesCount(uploads.size());
            storiesProgressView.setStoryDuration(15000L);
            picassoLoad(uploads.get(counter).getImage(), imageView, progressBar);
            storiesProgressView.startStories();
            storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
                @Override
                public void onNext() {
                    if(counter < uploads.size()){
                        counter++;
                        picassoLoad(uploads.get(counter).getImage(), imageView, progressBar);
                    }else{
                        createStories(view, uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                    }
                }

                @Override
                public void onPrev() {
                    if(counter > 0){
                        counter--;
                        picassoLoad(uploads.get(counter).getImage(), imageView, progressBar);
                    }else{
                        createStories(view, uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                    }
                }

                @Override
                public void onComplete() {
                    createStories(view, uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                }
            });

            viewFlipper.addView(view);
            btnPrev.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    storiesProgressView.reverse();
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    storiesProgressView.skip();
                }
            });

        }
    }

    private void picassoLoad(String src, final ImageView imageView, final ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(src).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void handleSwipes() {
        viewFlipper.setOnTouchListener(new SwipeListener(getContext()){
            public void onSwipeTop() {
                Toast.makeText(getContext(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                viewFlipper.showPrevious();
                Toast.makeText(getContext(), "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                viewFlipper.showNext();
                Toast.makeText(getContext(), "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(getContext(), "bottom", Toast.LENGTH_SHORT).show();
            }
        });
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
