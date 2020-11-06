package com.lysofts.luku.chat_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lysofts.luku.R;
import com.lysofts.luku.models.Match;
import com.squareup.picasso.Picasso;


public class ChatActivity extends AppCompatActivity {
    EditText txtMessage;
    ImageView btnSend;

    ImageView imgProfile;
    TextView tvName, tvTitle;
    Match match;

    String userId, name, userImage, title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_chat);
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);

        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);

        userId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("name");
        userImage = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        updateUI();
        textWatcher();
    }

    private void updateUI() {
        Picasso.get().load(userImage).placeholder(R.drawable.ic_baseline_account_circle_24).into(imgProfile);

        tvName.setText(name);
        tvTitle.setText(title);
    }

    private void textWatcher() {
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(txtMessage.getText())){
                    btnSend.setImageResource(R.drawable.ic_baseline_send_active );
                    btnSend.setEnabled(true);
                }else{
                    btnSend.setImageResource(R.drawable.ic_baseline_send_24);
                    btnSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void closeActivity(View view) {
        finish();
    }
}
