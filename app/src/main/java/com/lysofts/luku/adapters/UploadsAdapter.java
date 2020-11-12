package com.lysofts.luku.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lysofts.luku.PhotoviewerActivity;
import com.lysofts.luku.R;
import com.lysofts.luku.models.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UploadsAdapter extends RecyclerView.Adapter<UploadsAdapter.ViewHolder> {
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Context context;
    List<Upload> uploadList;

    public UploadsAdapter(Context context, List<Upload> uploadList) {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        this.context = context;
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.profile_upload_design, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setUpload(uploadList.get(position));
        holder.child.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoviewerActivity.class);
                intent.putExtra("imageUrl", uploadList.get(position).getImage());
                context.startActivity(intent);
            }
        }));

        holder.child.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_image_choices, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            deleteImage(uploadList.get(position).getId());
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
    }

    private void deleteImage(final String id) {
        databaseReference.child("users").child(mAuth.getUid()).child("uploads").child(id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("DELETED:::DB", id);
                    storageReference.child("uploads").child(id+".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("DELETED:::STORE", id+".jpg");
                            }else{
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View child;
        public ImageView imageView;
        public TextView tvClaps;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            child = itemView;
            imageView = child.findViewById(R.id.upload_pic);
            tvClaps = child.findViewById(R.id.claps);
        }

        public void setUpload(Upload upload){
            tvClaps.setText(String.valueOf(upload.getClaps()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(upload.getImage()).placeholder(R.drawable.ic_baseline_account_box_24).into(imageView);
        }
    }
}
