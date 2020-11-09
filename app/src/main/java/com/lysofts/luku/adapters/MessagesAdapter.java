package com.lysofts.luku.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
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
import com.lysofts.luku.constants.MyConstants;
import com.lysofts.luku.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private FirebaseAuth mAuth;
    public static final int  MESSAGE_TYPE_RECEIVED=0, MESSAGE_TYPE_SENT=1;
    List<Message> messages;
    String userImage;
    Context context;

    public MessagesAdapter(Context context, List<Message> messages, String userImage) {
        mAuth = FirebaseAuth.getInstance();
        this.messages = messages;
        this.context = context;
        this.userImage = userImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType){
            case MESSAGE_TYPE_RECEIVED:
                v = LayoutInflater.from(context).inflate(R.layout.chat_item_received, parent, false);
                break;
            case MESSAGE_TYPE_SENT:
                v = LayoutInflater.from(context).inflate(R.layout.chat_item_sent, parent, false);
                break;
            default:
                break;
        }
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.setMessage(message);
        if (position>0){
            if (messages.get(position-1).getSender().equals(message.getSender()) && !message.getSender().equals(mAuth.getUid())){
                holder.imgProfile.setImageDrawable(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View viewProfile;
        public ImageView imgProfile, imgStatus;
        public TextView tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            viewProfile  = itemView;
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void setMessage(Message message){
            tvMessage.setText(message.getText());
            tvTime.setText(MyConstants.getTime(message.getTime()));
            if (message.getSender().equals(mAuth.getUid())){
                if (!message.isRead()){
                    Drawable drawable = imgStatus.getDrawable();
                    drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                    imgStatus.setImageDrawable(drawable);
                }
            }else{
                Picasso.get().load(userImage)
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .into(imgProfile);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender().equals(mAuth.getUid())? MESSAGE_TYPE_SENT: MESSAGE_TYPE_RECEIVED;
    }
}
