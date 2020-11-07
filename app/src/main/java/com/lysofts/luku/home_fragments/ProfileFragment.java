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
import com.lysofts.luku.SignUp;
import com.lysofts.luku.local.MyProfile;
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
    List<Upload> uploadList = new ArrayList<>();

    CircleImageView profilePic;
    ImageView editProfilePic;
    TextView name1, title, name2, email, phone, dob, interestedIn;
    LinearLayout uploadsLayout;
    ProgressBar progressBar;
    LinearLayout profileLayout;
    Button btnAddPic, btnSignOut;

    String clickedImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,  container, false);
        progressBar = v.findViewById(R.id.progress_circular);
        profileLayout = v.findViewById(R.id.profile);
        btnAddPic = v.findViewById(R.id.btn_add_pic);
        profilePic = v.findViewById(R.id.profile_pic);
        editProfilePic = v.findViewById(R.id.edit_profile_pic);
        name1 = v.findViewById(R.id.tvName1);
        title = v.findViewById(R.id.tvTitle);
        uploadsLayout = v.findViewById(R.id.uploadsLayout);
        name2 = v.findViewById(R.id.tvName2);
        email = v.findViewById(R.id.tvEmail);
        phone = v.findViewById(R.id.tvPhone);
        dob = v.findViewById(R.id.tvDOB);
        interestedIn = v.findViewById(R.id.tvInterestedIn);
        btnSignOut = v.findViewById(R.id.btn_sign_out);
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

        loadUploads();
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).signOut();
            }
        });
    }
    private void loadUploads() {
        databaseReference.child("users").child(mAuth.getUid()).child("uploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Upload upload = snapshot1.getValue(Upload.class);
                    uploadList.add(upload);
                }
                manageUploads();
                progressBar.setVisibility(View.GONE);
                profileLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI() {
        Picasso.get().load(userProfile.getImage()).placeholder(R.drawable.ic_baseline_account_circle_24).into(profilePic);
        name1.setText(userProfile.getName());
        title.setText(userProfile.getTitle());
        name2.setText(userProfile.getName());
        email.setText(userProfile.getEmail());
        phone.setText(userProfile.getPhone());
        dob.setText(userProfile.getBirthday());
        interestedIn.setText(userProfile.getInterestedIn());

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

    private void manageUploads() {
        uploadsLayout.removeAllViews();
        if(getActivity()!=null){
            LayoutInflater inf = LayoutInflater.from(getActivity());
            View child;
            if (uploadList != null && uploadList.size()>=3){
                btnAddPic.setVisibility(View.GONE);
            }else{
                btnAddPic.setVisibility(View.VISIBLE);
            }

            for (Upload upload:uploadList){
                child = inf.inflate(R.layout.profile_upload_design, null);
                ImageView imageView = child.findViewById(R.id.upload_pic);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get().load(upload.getImage()).into(imageView);
                TextView tvClaps = child.findViewById(R.id.claps);
                tvClaps.setText(String.valueOf(upload.getClaps()));
                child.setOnClickListener(viewImage(upload.getImage()));
                child.setOnLongClickListener(editUpload(upload.getId()));
                uploadsLayout.addView(child);
            }
        }
    }

    private View.OnClickListener viewImage(final String image) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PhotoviewerActivity.class);
                intent.putExtra("imageUrl", image);
                startActivity(intent);
            }
        };
    }

    private View.OnLongClickListener editUpload(final String id) {
        clickedImage = id;
        return  new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_image_choices, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                deleteImage();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                return false;
            }
        };
    }

    private void deleteImage() {
        databaseReference.child("users").child(mAuth.getUid()).child("uploads").child(clickedImage).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("DELETED:::DB", clickedImage);
                    storageReference.child("uploads").child(clickedImage+".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("DELETED:::STORE", clickedImage+".jpg");
                            }else{
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
