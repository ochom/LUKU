package com.lysofts.luku.constants;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.joda.time.DateTimeComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class MyConstants {
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
            LocalDate l1 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                l1 = LocalDate.of(year, month, date);
                LocalDate now1 = LocalDate.now();
                Period diff1 = Period.between(l1, now1);
                age = diff1.getYears();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return age;

    }

    public static String getTime(String time) {
        String editedDate;
        SimpleDateFormat simpleDF;
        try {
            simpleDF = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now   = new Date();
            Date date2 = simpleDF.parse(time);
            simpleDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (DateTimeComparator.getDateOnlyInstance().compare(now, date2)>0){
                editedDate = simpleDF.format(date2).substring(0,8);
            }else{
                editedDate = simpleDF.format(date2).substring(11,16);
            }
        } catch (ParseException e) {
            editedDate="";
            e.printStackTrace();
        }
        return editedDate;
    }
}
