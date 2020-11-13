package com.lysofts.luku.sign_in;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lysofts.luku.R;

public class PhoneVerificationFragment extends Fragment {
    TextView tvTimer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin_mobile_verification, container, false);
        tvTimer = v.findViewById(R.id.tvTimer);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CountDownTimer timer = new CountDownTimer(60000, 1000){

            @Override
            public void onTick(long l) {
                    tvTimer.setText(""+ l/1000);
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }
}
