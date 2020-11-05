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

public class GenderFragment extends Fragment {
    Button btnContinue;
    RadioGroup radioGroup;
    RadioButton checkedButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gender, container, false);
        btnContinue = v.findViewById(R.id.btn_continue);
        radioGroup = v.findViewById(R.id.gender_choice_radio_group);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sex = "";
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id){
                    case  R.id.woman:
                        sex = "woman";
                        break;
                    default:
                        sex = "man";
                        break;
                }
                ((SignUp) getActivity()).setSex(sex);
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
