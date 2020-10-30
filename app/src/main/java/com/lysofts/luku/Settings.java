package com.lysofts.luku;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

public class Settings extends AppCompatActivity {

    CrystalRangeSeekbar ageSeeker;
    TextView tvMin, tvMax, tvDistance;

    SeekBar distanceSeeker;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ageSeeker  = findViewById(R.id.age_seeker);
        tvMin = findViewById(R.id.txtMin);
        tvMax = findViewById(R.id.txtMax);


        distanceSeeker = findViewById(R.id.distance_seeker);
        tvDistance = findViewById(R.id.txt_distance);

        handleAgeSeeker();
        handleDistanceSeeker();
    }

    private void handleAgeSeeker() {
        ageSeeker.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));
            }
        });

        ageSeeker.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                //Save valued to db
            }
        });
    }

    private void handleDistanceSeeker() {
        distanceSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvDistance.setText(i +" Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
