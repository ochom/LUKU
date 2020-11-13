package com.lysofts.luku.sign_in;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lysofts.luku.R;
import com.lysofts.luku.SignUp;

public class PhoneVerificationFragment extends Fragment {
    TextView tvTimer;
    EditText txtCode;
    Button btnSubmit, btnResendCode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin_mobile_verification, container, false);
        tvTimer = v.findViewById(R.id.tvTimer);
        txtCode = v.findViewById(R.id.txtCode);
        btnSubmit = v.findViewById(R.id.btn_submit);
        btnResendCode = v.findViewById(R.id.btn_resendCode);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnResendCode.setVisibility(View.GONE);
        CountDownTimer timer = new CountDownTimer(60000, 1000){

            @Override
            public void onTick(long l) {
                    tvTimer.setText(""+ l/1000);
            }

            @Override
            public void onFinish() {
                btnResendCode.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = txtCode.getText().toString();
                if (!TextUtils.isEmpty(code)){
                    ((SignUp) getActivity()).submitOTP(code);
                }
            }
        });
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SignUp) getActivity()).resendOTP();
            }
        });
    }
}
