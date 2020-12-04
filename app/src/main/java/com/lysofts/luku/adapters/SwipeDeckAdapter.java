package com.lysofts.luku.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lysofts.luku.R;
import com.lysofts.luku.constants.MyConstants;
import com.lysofts.luku.models.SwipeModel;
import com.lysofts.luku.models.Upload;
import com.lysofts.luku.models.UserProfile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class SwipeDeckAdapter extends BaseAdapter {
    Context context;
    List<SwipeModel> models;
    int counter=0;

    public SwipeDeckAdapter(Context context, List<SwipeModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.person_preview, viewGroup, false);
            ImageView profileImageView = view.findViewById(R.id.profile_pic);
            TextView txtName = view.findViewById(R.id.tvName);
            TextView txtTitle = view.findViewById(R.id.tvTitle);
            ProgressBar progressBar = view.findViewById(R.id.progress_circular);
            ImageView imageView = view.findViewById(R.id.image);
            View btnPrev = view.findViewById(R.id.btnPrev);
            View btnNext = view.findViewById(R.id.btnNext);

            StoriesProgressView storiesProgressView = view.findViewById(R.id.stories);

            int age = MyConstants.getAge(models.get(i).getUser().getBirthday());

            Picasso.get().load(models.get(i).getUser().getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(profileImageView);
            txtName.setText(String.format("%s, %d",models.get(i).getUser().getName(), age));
            txtTitle.setText(models.get(i).getUser().getTitle());
            createStories(models.get(i).getUser(), models.get(i).getUploads(), storiesProgressView, imageView, progressBar, btnPrev, btnNext);
        }
        return view;
    }

    private void createStories(final UserProfile userProfile, final List<Upload> uploads, final StoriesProgressView storiesProgressView, final ImageView imageView, final ProgressBar progressBar, final View btnPrev, final View btnNext){
        if(uploads!=null && uploads.size()>0){
            counter=0;
            storiesProgressView.setStoriesCount(uploads.size());
            storiesProgressView.setStoryDuration(15000L);
            picassoLoad(userProfile, uploads.get(counter), imageView, progressBar);
            storiesProgressView.startStories();
            storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
                @Override
                public void onNext() {
                    if(counter < uploads.size()){
                        counter++;
                        picassoLoad(userProfile, uploads.get(counter), imageView, progressBar);
                    }else{
                        createStories(userProfile,  uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                    }
                }

                @Override
                public void onPrev() {
                    if(counter > 0){
                        counter--;
                        picassoLoad(userProfile, uploads.get(counter), imageView, progressBar);
                    }else{
                        createStories(userProfile, uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                    }
                }

                @Override
                public void onComplete() {
                    createStories(userProfile, uploads, storiesProgressView, imageView, progressBar, btnPrev, btnNext);
                }
            });
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

    private void picassoLoad(UserProfile user, final Upload upload, ImageView imageView, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(upload.getImage()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user.getId()).child("uploads").child(upload.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int claps = snapshot.child("claps").getValue(Integer.class);
                //snapshot.child("claps").getRef().setValue(claps+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
