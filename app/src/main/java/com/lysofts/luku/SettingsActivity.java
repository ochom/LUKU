package com.lysofts.luku;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lysofts.luku.local.MyProfile;
import com.lysofts.luku.models.UserProfile;

public class SettingsActivity extends AppCompatActivity {
    Button btnSignOut;
    UserProfile userProfile;

    EditText txtName, txtTitle;
    RadioGroup genderRadio, interestedInRadio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userProfile = new MyProfile(this).getProfile();

        txtName = findViewById(R.id.txtName);
        txtTitle = findViewById(R.id.txtTitle);
        genderRadio = findViewById(R.id.gender_radio_group);
        interestedInRadio = findViewById(R.id.interested_in_radio_group);

        updateUI();
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                new MyProfile(SettingsActivity.this).deleteProfile();
                finishAffinity();
            }
        });
    }

    private void updateUI() {
        txtName.setText(userProfile.getName());
        txtTitle.setText(userProfile.getTitle());
        int genderC = 0;
        if (userProfile.getSex().equals("man")){
            genderC = R.id.man;
        }else{
            genderC = R.id.woman;
        }

        int interestedInC = 0;
        if (userProfile.getInterestedIn().equals("men")){
            interestedInC = R.id.men;
        }else if (userProfile.getInterestedIn().equals("women")){
            interestedInC = R.id.women;
        }else{
            interestedInC = R.id.any;
        }

        genderRadio.check(genderC);
        interestedInRadio.check(interestedInC);
    }

    public void closeActivity(View view) {
        finish();
    }

    public void updateProfile(View view) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String name, title, sex, interestedIn;
        name = txtName.getText().toString();
        title = txtTitle.getText().toString();
        sex = genderRadio.getCheckedRadioButtonId()==R.id.man? "man": "woman";
        int selectedInterest = interestedInRadio.getCheckedRadioButtonId();

        if (selectedInterest ==R.id.men){
            interestedIn="men";
        }else if (selectedInterest ==R.id.women){
            interestedIn="women";
        }else{
            interestedIn="any";
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userProfile.getId()).child("name").setValue(name);
        databaseReference.child("users").child(userProfile.getId()).child("title").setValue(title);
        databaseReference.child("users").child(userProfile.getId()).child("sex").setValue(sex);
        databaseReference.child("users").child(userProfile.getId()).child("interestedIn").setValue(interestedIn);

        userProfile.setName(name);
        userProfile.setTitle(title);
        userProfile.setSex(sex);
        userProfile.setInterestedIn(interestedIn);
        new MyProfile(this).setProfile(userProfile);

        if (progressDialog.isShowing()){
            progressDialog.dismiss();
            finish();
        }
    }
}
