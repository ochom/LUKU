package com.lysofts.luku.home_fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lysofts.luku.MainActivity;
import com.lysofts.luku.PhotoviewerActivity;
import com.lysofts.luku.R;
import com.lysofts.luku.SettingsActivity;
import com.lysofts.luku.SignUp;
import com.lysofts.luku.adapters.UploadsAdapter;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.Match;
import com.lysofts.luku.models.Upload;
import com.lysofts.luku.models.UserProfile;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    UserProfile userProfile;
    List<Upload> uploadList;

    CircleImageView profilePic;
    ImageView editProfilePic;
    TextView tvName, tvTitle, tvSent, tvReceived, tvMatched;
    LinearLayout uploadsLayout;
    ProgressBar progressBar;
    Button btnAddPic, btnEditProfile, btnSignOut;

    RecyclerView recyclerView;
    UploadsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,  container, false);
        progressBar = v.findViewById(R.id.progress_circular);
        btnAddPic = v.findViewById(R.id.btn_add_pic);
        btnEditProfile = v.findViewById(R.id.btn_edit_profile);
        profilePic = v.findViewById(R.id.profile_pic);
        editProfilePic = v.findViewById(R.id.edit_profile_pic);
        tvName = v.findViewById(R.id.tvName);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvSent = v.findViewById(R.id.tvSent);
        tvReceived = v.findViewById(R.id.tvReceived);
        tvMatched = v.findViewById(R.id.tvMatched);
        uploadsLayout = v.findViewById(R.id.uploadsLayout);
        btnSignOut = v.findViewById(R.id.btn_sign_out);


        recyclerView = v.findViewById(R.id.uploadsList);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userProfile = new MyProfile(getActivity()).getProfile();
        updateUI();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        monitorOnlineAccount();
        getMatches();

        getUploads();
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
    }

    private void getMatches() {
        databaseReference.child("matches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sent=0, received=0, matched=0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Match match = dataSnapshot.getValue(Match.class);
                    if (match.getType().equals("like")){
                        if (match.getSender().getId().equals(mAuth.getUid())){
                            sent++;
                        }
                        if (!match.getSender().getId().equals(mAuth.getUid())){
                            received++;
                        }
                        if (match.getStatus().equals("matched")){
                            matched++;
                        }
                    }

                }
                tvSent.setText(String.valueOf(sent));
                tvReceived.setText(String.valueOf(received));
                tvMatched.setText(String.valueOf(matched));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUploads() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        databaseReference.child("users").child(mAuth.getUid()).child("uploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    uploadList.add(upload);
                }
                adapter = new UploadsAdapter(getActivity(), uploadList);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void monitorOnlineAccount(){
        databaseReference.child("users").child(userProfile.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile = snapshot.getValue(UserProfile.class);
                if (userProfile !=null){
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI() {
        Picasso.get().load(userProfile.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(profilePic);
        tvName.setText(userProfile.getName());
        tvTitle.setText(userProfile.getTitle());

        profilePic.setOnClickListener(viewImage(userProfile.getImage()));
        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfileImage();
            }
        });
        btnAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUploadImage();
            }
        });
    }

    private void selectProfileImage() {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(getContext());
        startActivityForResult(intent, 443);
    }

    public View.OnClickListener viewImage(final String image) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PhotoviewerActivity.class);
                intent.putExtra("imageUrl", image);
                startActivity(intent);
            }
        };
    }

    private void selectUploadImage() {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(getContext());
        startActivityForResult(intent, 444);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 443) {//change profile pic
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profilePic.setImageURI(resultUri);
                updateProfilePic(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == 444) {//upload images
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void updateProfilePic(Uri imageUri) {
        final StorageReference filePath = storageReference.child("profiles").child(mAuth.getUid()+".jpg");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        //uploading the image
        final UploadTask uploadTask = filePath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            String image = task.getResult().toString();
                            updateProfile(image);
                        }else{
                            Toast.makeText(getContext(), "Error: "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void updateProfile(String image) {
        databaseReference.child("users").child(mAuth.getUid()).child("image").setValue(image);
    }

    private void uploadImage(Uri imageUri) {
        final String pic_id = generateID();
        final StorageReference filePath = storageReference.child("uploads").child(pic_id+".jpg");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        //uploading the image
        final UploadTask uploadTask = filePath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            String image = task.getResult().toString();
                            updateUploads(pic_id, image);
                        }else{
                            Toast.makeText(getContext(), "Error: "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private String generateID() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddYYYY");
        String currentDate = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssa");
        String currentTime = timeFormat.format(calendar.getTime());
        return mAuth.getUid()+currentDate+currentTime;
    }

    private void updateUploads(String id, String image) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("image", image);
        data.put("claps", 0);
        databaseReference.child("users").child(mAuth.getUid()).child("uploads").child(id).updateChildren(data);
    }
}
