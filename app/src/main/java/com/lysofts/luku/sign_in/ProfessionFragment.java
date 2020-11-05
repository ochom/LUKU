package com.lysofts.luku.sign_in;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lysofts.luku.R;
import com.lysofts.luku.SignUp;

public class ProfessionFragment extends Fragment {
    Button btnContinue;
    RadioGroup radioGroup;
    EditText txtProfession;
    String profession = "", title="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profession, container, false);
        btnContinue = v.findViewById(R.id.btn_continue);
        radioGroup = v.findViewById(R.id.gender_choice_radio_group);
        txtProfession = v.findViewById(R.id.txtProfession);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = txtProfession.getText().toString();
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id){
                    case  R.id.student:
                        profession = "Student";
                        title = "Student, "+title;
                        break;
                    default:
                        profession = "Professional";
                        break;
                }
                ((SignUp) getActivity()).setProfession(profession, title);
            }
        });

        txtProfession.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(txtProfession.getText())){
                    btnContinue.setBackgroundResource(R.drawable.button_design_bg);
                    btnContinue.setEnabled(true);
                }else{
                    btnContinue.setBackgroundResource(R.drawable.disabled_button);
                    btnContinue.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id){
                    case R.id.professional:
                        txtProfession.setHint("Job title");
                        break;
                    default:
                        txtProfession.setHint("University of College");
                        break;
                }
                txtProfession.setVisibility(View.VISIBLE);
            }
        });
    }
}
