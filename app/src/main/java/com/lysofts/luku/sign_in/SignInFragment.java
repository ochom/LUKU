package com.lysofts.luku.sign_in;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lysofts.luku.R;
import com.lysofts.luku.SignUp;

public class SignInFragment extends Fragment {
    Button btnSignIn;
    EditText txtPhone;
    String lastChar = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin_mobile, container, false);
        txtPhone = v.findViewById(R.id.txtPhone);
        btnSignIn = v.findViewById(R.id.btn_sign_in);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = txtPhone.getText().toString().length();
                if (digits > 1)
                    lastChar = txtPhone.getText().toString().substring(digits-1);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = txtPhone.getText().toString().length();
                if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        txtPhone.append("-");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = txtPhone.getText().toString();
                ((SignUp) getActivity()).signInWithPhone(phone);
                //((SignUp) getActivity()).signInOrRegister(email, password);
            }
        });
    }
}
