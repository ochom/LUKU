package com.lysofts.luku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.UserProfile;
import com.lysofts.luku.sign_in.AddPicFragment;
import com.lysofts.luku.sign_in.DoBFragment;
import com.lysofts.luku.sign_in.GenderFragment;
import com.lysofts.luku.sign_in.InterestFragment;
import com.lysofts.luku.sign_in.ProfessionFragment;
import com.lysofts.luku.sign_in.SignInFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    String name, profession, title, birthday,sex, interestedIn, phone, image;
    Uri imageUri;



    ProgressDialog progressDialog;

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

        FacebookLogin();
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

    private void FacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){

            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest   =   GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("JSON", ""+response.getJSONObject().toString());
                        try{
                            name        =   object.getString("name");
                            if(object.has("birthday")){
                                birthday = object.getString("birthday"); // 01/31/1980 format
                            }
                            LoginManager.getInstance().logOut();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("DATA:::", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DATA:::", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkProfile(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DATA:::", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void facebookLogin(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_birthday", "user_friends"));
    }

    public void signInOrRegister(final String email, final String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write you email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please create a password", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Authenticating. Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            if (task.isSuccessful()){
                                checkProfile(mAuth.getCurrentUser());
                            } else {
                                registerUser(email, password);
                            }
                        }
                    }
            );
        }
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


    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            checkProfile(mAuth.getCurrentUser());
                        }else{
                            // If sign in fails, display a message to the user.
                            //Toast.makeText(SignUp.this, "Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            Toast.makeText(SignUp.this, "Authentication failed. Try again. ",Toast.LENGTH_SHORT).show();
                        }
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
        loadFragment(new AddPicFragment());
    }

    public void completeSignUp(String name, String phone, Uri imageUri) {
        this.name  = name;
        this.phone = phone;
        this.imageUri = imageUri;
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Your name is required", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Your mobile number is required", Toast.LENGTH_SHORT).show();
        }else if(imageUri == null){
            Toast.makeText(this, "Add a profile picture", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Finalizing. Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            uploadImage();
        }
    }

    private void uploadImage() {
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
                    startActivity(new Intent(SignUp.this, MainActivity.class));
                }else{
                    Toast.makeText(SignUp.this, "Registration failed. Try again", Toast.LENGTH_SHORT).show();
                }
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        });
    }
}