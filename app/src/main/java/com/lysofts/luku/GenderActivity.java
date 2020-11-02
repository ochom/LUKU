package com.lysofts.luku;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class GenderActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button buttonContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gender);

        buttonContinue = findViewById(R.id.btn_continue);
        radioGroup = findViewById(R.id.gender_choice_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioButton != null){
                    radioButton.setTextColor(Color.parseColor("#070707"));
                    radioButton.setBackground(getResources().getDrawable(R.drawable.gender_button_choice));
                }
                radioButton = group.findViewById(checkedId);
                radioButton.setTextColor(Color.parseColor("#E91E63"));
                radioButton.setBackground(getResources().getDrawable(R.drawable.gender_button_choice_select));

                buttonContinue.setBackground(getResources().getDrawable(R.drawable.button_design_bg));
                buttonContinue.setEnabled(true);
                buttonContinue.setTextColor(Color.WHITE);
            }
        });
    }
}
