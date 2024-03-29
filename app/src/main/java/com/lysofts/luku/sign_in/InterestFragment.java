package com.lysofts.luku.sign_in;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lysofts.luku.R;
import com.lysofts.luku.SignUp;

public class InterestFragment extends Fragment {
    Button btnContinue;
    RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_interested_in, container, false);btnContinue = v.findViewById(R.id.btn_continue);
        radioGroup = v.findViewById(R.id.gender_choice_radio_group);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String interestedIn = "";
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id){
                    case  R.id.men:
                        interestedIn = "men";
                        break;
                    case  R.id.women:
                    default:
                        interestedIn = "women";
                        break;
                }
                ((SignUp) getActivity()).setInterestedIn(interestedIn);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                btnContinue.setBackgroundResource(R.drawable.button_design_bg);
                btnContinue.setEnabled(true);
            }
        });
    }
}
