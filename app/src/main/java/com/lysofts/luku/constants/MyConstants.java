package com.lysofts.luku.constants;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class MyConstants {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getAge(String dob) {
        int age=0;
        try {
            Date d= null;
            d = new SimpleDateFormat("MM/dd/yyyy").parse(dob);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int date = c.get(Calendar.DATE);
            LocalDate l1 = LocalDate.of(year, month, date);
            LocalDate now1 = LocalDate.now();
            Period diff1 = Period.between(l1, now1);
            age = diff1.getYears();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return age;

    }
}
