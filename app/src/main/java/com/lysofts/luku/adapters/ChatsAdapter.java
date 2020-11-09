package com.lysofts.luku.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.lysofts.luku.ChatActivity;
import com.lysofts.luku.constants.MyConstants;
import com.lysofts.luku.models.Chat;
import com.lysofts.luku.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private FirebaseAuth mAuth;

    List<Chat> chats;
    Context context;

    public ChatsAdapter(Context context, List<Chat> chats) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.chat_design, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Chat chat = chats.get(position);
        holder.setChat(chat);
        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userId", chat.getProfile().getId());
            intent.putExtra("image",chat.getProfile().getImage());
            intent.putExtra("name", chat.getProfile().getName());
            intent.putExtra("title", chat.getProfile().getTitle());
            context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View viewProfile;
        public ImageView imgProfile, imgStatus;
        public TextView tvName, tvLastMessage, tvTime,tvNewMessagesCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            viewProfile  = itemView;
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvNewMessagesCount = itemView.findViewById(R.id.tvNewMessagesCount);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void setChat(Chat chat){
            Picasso.get().load(chat.getProfile()
                    .getImage())
                    .placeholder(R.drawable.ic_baseline_account_circle_24).into(imgProfile);
            tvName.setText(chat.getProfile().getName());
            tvLastMessage.setText("No chats yet");
            imgStatus.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
            tvNewMessagesCount.setVisibility(View.GONE);

            if (chat.getLastMessage()!=null){
                Message message = chat.getLastMessage();
                tvLastMessage.setText(message.getText().length()>30?message.getText().substring(0,20)+"...":message.getText());
                if (message.getSender().equals(mAuth.getUid())){
                    imgStatus.setVisibility(View.VISIBLE);
                }
                if (!message.isRead()){
                    Drawable drawable = imgStatus.getDrawable();
                    drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                    imgStatus.setImageDrawable(drawable);
                }
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(MyConstants.getTime(message.getTime()));
            }

            if (chat.getMessages()!=null && chat.getMessages().size()>0){
                int totalNew=0;
                for (Map.Entry<String, Message> entry: chat.getMessages().entrySet()){
                    Message message = entry.getValue();
                    if (!message.isRead() && !message.getSender().equals(mAuth.getUid())){
                        totalNew++;
                    }
                }
                if (totalNew>0){
                    tvNewMessagesCount.setVisibility(View.VISIBLE);
                    tvNewMessagesCount.setText(String.valueOf(totalNew));
                }
            }
        }
    }
}
