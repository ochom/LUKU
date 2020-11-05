package com.lysofts.luku.view_holders;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.lysofts.luku.R;
import com.lysofts.luku.constants.MyConstants;
import com.lysofts.luku.models.Match;
import com.squareup.picasso.Picasso;

public class MatchViewHolder extends RecyclerView.ViewHolder {
    public View viewProfile;
    public ImageView imgProfile, imgStatus, imgAction;
    public TextView tvName, tvTitle, tvStatus;

    public MatchViewHolder(@NonNull View itemView) {
        super(itemView);
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
        Picasso.get().load(match.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(imgProfile);
        tvName.setText(match.getName()+", "+ MyConstants.getAge(match.getBirthday()));
        tvTitle.setText(match.getTitle());
        tvStatus.setText(match.getStatus());
        if (match.getStatus().equals("sent") || match.getStatus().equals("matched")){
            imgAction.setVisibility(View.GONE);
        }

        if (match.getStatus().equals("sent")){
            Drawable drawable = imgStatus.getDrawable();
            drawable.setColorFilter(new LightingColorFilter(Color.parseColor("#D01760"), Color.parseColor("#D01760")));
            imgStatus.setImageDrawable(drawable);
            tvStatus.setTextColor(Color.parseColor("#D01760"));
        }

        if (match.getStatus().equals("received")){
            Drawable drawable = imgStatus.getDrawable();
            drawable.setColorFilter(new LightingColorFilter(Color.RED, Color.RED));
            imgStatus.setImageDrawable(drawable);
            tvStatus.setTextColor(Color.RED);
        }

        if (match.getStatus().equals("matched")){
            imgStatus.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

    }
}
