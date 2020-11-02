package com.lysofts.luku.chat_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lysofts.luku.R;


public class ChatActivity extends AppCompatActivity {

    EditText txtMessage;
    ImageView btnSend;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_chat);
        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);
        textWatcher();
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
