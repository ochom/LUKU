package com.lysofts.luku.signin_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lysofts.luku.R;
import com.lysofts.luku.SignUp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DoBFragment  extends Fragment {
    Button btnContinue;
    DatePicker datePicker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dob, container, false);
        datePicker = v.findViewById(R.id.datePicker);
        btnContinue = v.findViewById(R.id.btn_continue);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        datePicker.setMaxDate(c.getTimeInMillis());

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateOfBirth = (datePicker.getMonth()+1)+"/"+datePicker.getDayOfMonth()+"/"+datePicker.getYear();
                ((SignUp) getActivity()).setDateOfBirth(dateOfBirth);
            }
        });
    }
}
