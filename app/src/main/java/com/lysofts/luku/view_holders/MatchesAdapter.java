package com.lysofts.luku.view_holders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lysofts.luku.R;
import com.lysofts.luku.chat_app.ChatActivity;
import com.lysofts.luku.constants.MyConstants;
import com.lysofts.luku.firebase.Matches;
import com.lysofts.luku.models.Match;
import com.lysofts.luku.models.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
    private FirebaseAuth mAuth;

    List<Match> matches;
    Context context;

    public MatchesAdapter(Context context, List<Match> matches) {
        this.matches = matches;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.matches_design, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Match match = matches.get(position);
        holder.setMatch(match);
        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userId", match.getSender().getId().equals(mAuth.getUid())?match.getReceiver().getId():match.getSender().getImage());
            intent.putExtra("image", match.getSender().getId().equals(mAuth.getUid())?match.getReceiver().getImage():match.getSender().getImage());
            intent.putExtra("name", match.getSender().getId().equals(mAuth.getUid())?match.getReceiver().getName():match.getSender().getName());
            intent.putExtra("title", match.getSender().getId().equals(mAuth.getUid())?match.getReceiver().getTitle():match.getSender().getTitle());
            context.startActivity(intent);
            }
        });

        holder.imgAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Matches.confirmMatch(match.getSender().getId(), match.getReceiver().getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View viewProfile;
        public ImageView imgProfile, imgStatus, imgAction;
        public TextView tvName, tvTitle, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            viewProfile  = itemView;
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            imgAction = itemView.findViewById(R.id.imgAction);
            tvName = itemView.findViewById(R.id.tvName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setMatch(Match match){
            UserProfile sender = match.getSender();
            UserProfile receiver = match.getReceiver();
            if (sender.getId().equals(mAuth.getUid())){
                Picasso.get().load(receiver.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(imgProfile);
                tvName.setText(String.format("%s, %d", receiver.getName(), MyConstants.getAge(receiver.getBirthday())));
                tvTitle.setText(receiver.getTitle());
                imgAction.setVisibility(View.GONE);
            }else{
                Picasso.get().load(sender.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(imgProfile);
                tvName.setText(String.format("%s, %d", sender.getName(), MyConstants.getAge(sender.getBirthday())));
                tvTitle.setText(sender.getTitle());
            }

            if (sender.getId().equals(mAuth.getUid()) && match.getStatus().equals("pending")){
                Drawable drawable = imgStatus.getDrawable();
                drawable.setColorFilter(new LightingColorFilter(Color.parseColor("#D01760"), Color.parseColor("#D01760")));
                imgStatus.setImageDrawable(drawable);
                tvStatus.setTextColor(Color.parseColor("#D01760"));
                tvStatus.setText(R.string.match_status_sent);
            }else if (!sender.getId().equals(mAuth.getUid()) && match.getStatus().equals("pending")){
                Drawable drawable = imgStatus.getDrawable();
                drawable.setColorFilter(new LightingColorFilter(Color.RED, Color.RED));
                imgStatus.setImageDrawable(drawable);
                tvStatus.setTextColor(Color.RED);
                tvStatus.setText(R.string.match_status_received);
            }else{
                imgStatus.setImageResource(R.drawable.ic_baseline_favorite_24);
                imgAction.setVisibility(View.GONE);
                tvStatus.setText(R.string.match_status_matched);
                tvStatus.setTextColor(Color.parseColor("#019207"));
            }
        }
    }
}
