package com.lysofts.luku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lysofts.luku.firebase.Matches;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.UserProfile;
import com.lysofts.luku.sign_in.AddNameFragment;
import com.lysofts.luku.sign_in.AddPictureFragment;
import com.lysofts.luku.sign_in.DoBFragment;
import com.lysofts.luku.sign_in.GenderFragment;
import com.lysofts.luku.sign_in.InterestFragment;
import com.lysofts.luku.sign_in.PhoneVerificationFragment;
import com.lysofts.luku.sign_in.ProfessionFragment;
import com.lysofts.luku.sign_in.SignInFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    String name, profession, title, birthday,sex, interestedIn, phone, image;
    Uri imageUri, firstUpload;



    ProgressDialog progressDialog;
    private String TAG="LUKU>>>";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        String state = getIntent().getStringExtra("state");


        if (state.equals("create_profile")){
            loadFragment(new GenderFragment());
        }else if(state.equals("register_user")){
            loadFragment(new SignInFragment());
        }

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
    }

    public void loadFragment(Object fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.fade_out
                    )
                    .replace(R.id.fragment_container, (Fragment) fragment)
                    .commit();
        }
    }

    public void signInWithPhone(String phoneNumber) {
        this.phone = phoneNumber;
//        phone = "797969142";
        phone = cleanPhone(phone);
        if (phone.length()<9){
            return;
        }
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        phone = "+254"+phone;
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks())
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


        //Testing
//        String smsCode = "111222";
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
//
//        // Configure faking the auto-retrieval with the whitelisted numbers.
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phone, smsCode);
//
//        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                .setPhoneNumber(phone)
//                .setTimeout(60L, TimeUnit.SECONDS)
//                .setActivity(this)
//                .setCallbacks(mCallbacks())
//                .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private String cleanPhone(String phone) {
        phone = phone.replaceAll("\\+","").replaceAll("-","");
        return phone.trim();
    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks(){
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("LUKU", "Verification code sent");
                signInWithPhoneAuthCredential(phoneAuthCredential);
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("LUKU", "Verification failed");
                Toast.makeText(SignUp.this, "Phone verification failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;
                loadFragment(new PhoneVerificationFragment());
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(SignUp.this, "Code auto retrieval failed", Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        };
    }


    public void submitOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    public void resendOTP() {
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks())
                        .setForceResendingToken(mResendToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            checkProfile(user);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                loadFragment(new SignInFragment());
                            }
                        }
                    }
                });

    }

    private void checkProfile(final FirebaseUser user) {
        databaseReference.child("users")
            .child(user.getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        UserProfile userProfile = snapshot.getValue(UserProfile.class);
                        new MyProfile(SignUp.this).setProfile(userProfile);
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                        finish();
                    }else{
                        loadFragment(new GenderFragment());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }


    public void setSex(String sex) {
        this.sex = sex;
        loadFragment(new InterestFragment());
    }

    public void setInterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
        loadFragment(new DoBFragment());
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.birthday = dateOfBirth;
        loadFragment(new ProfessionFragment());
    }

    public void setProfession(String profession, String title) {
        this.profession=profession;
        this.title = title;
        loadFragment(new AddNameFragment());
    }

    public void setNameAndProfilePic(String name, Uri imageUri) {
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Your name is required", Toast.LENGTH_SHORT).show();
        }else if(imageUri == null){
            Toast.makeText(this, "Add a profile picture", Toast.LENGTH_SHORT).show();
        }else{
            this.name  = name;
            this.imageUri = imageUri;
            loadFragment(new AddPictureFragment());
        }
    }

    public void completeSignUp(Uri firstUpload) {
        this.firstUpload = firstUpload;
        if(firstUpload == null){
            Toast.makeText(this, "You must add at least one default picture", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Finalizing. Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {
        final StorageReference filePath = storageReference.child("profiles").child(mAuth.getUid()+".jpg");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
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
                Toast.makeText(SignUp.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            progressDialog.dismiss();
                            throw task.getException();
                        }
                        image = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            image = task.getResult().toString();
                            createProfile();
                        }else{
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Toast.makeText(SignUp.this, "Error: "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void createProfile() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", mAuth.getUid());
        data.put("name", name);
        data.put("title", title);
        data.put("profession", profession);
        data.put("email", mAuth.getCurrentUser().getEmail());
        data.put("sex", sex);
        data.put("interestedIn", interestedIn);
        data.put("birthday", birthday);
        data.put("phone", phone);
        data.put("image", image);

        databaseReference.child("users").child(mAuth.getUid()).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    UserProfile userProfile = new UserProfile();
                    userProfile.setId(mAuth.getUid());
                    userProfile.setName(name);
                    userProfile.setTitle(title);
                    userProfile.setProfession(profession);
                    userProfile.setEmail( mAuth.getCurrentUser().getEmail());
                    userProfile.setSex(sex);
                    userProfile.setInterestedIn(interestedIn);
                    userProfile.setBirthday(birthday);
                    userProfile.setPhone(phone);
                    userProfile.setImage(image);
                    new MyProfile(SignUp.this).setProfile(userProfile);
                    Matches.sendNewUsers(mAuth.getUid(), sex, interestedIn);
                    startActivity(new Intent(SignUp.this, MainActivity.class));
                    uploadFirstUpload();
                }else{
                    Toast.makeText(SignUp.this, "Registration failed. Try again", Toast.LENGTH_SHORT).show();
                }
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void uploadFirstUpload() {
        final String pic_id = generateID();
        final StorageReference filePath = storageReference.child("uploads").child(pic_id+".jpg");
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(SignUp.this.getContentResolver(), firstUpload);
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
                Toast.makeText(SignUp.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignUp.this, "Error: "+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
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