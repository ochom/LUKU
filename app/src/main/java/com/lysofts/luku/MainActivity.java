package com.lysofts.luku;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.lysofts.luku.home_fragments.ChatsFragment;
import com.lysofts.luku.home_fragments.HomeFragment;
import com.lysofts.luku.home_fragments.MatchesFragment;
import com.lysofts.luku.home_fragments.ProfileFragment;
import com.lysofts.luku.services.NotificationService;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    Fragment fragment1, fragment2, fragment3, fragment4, activeFragment;
    FragmentManager fm;
    ConstraintLayout bottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    Button btnCloseBottomSheet, btnOpenSubscriptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        fragment1 = new HomeFragment();
        fragment2 = new MatchesFragment();
        fragment3 = new ChatsFragment();
        fragment4 = new ProfileFragment();
        fm = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        bottomSheet  = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        btnCloseBottomSheet = findViewById(R.id.close_bottom_sheet);
        btnOpenSubscriptions = findViewById(R.id.open_subscriptions);
        btnCloseBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        btnOpenSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        if (savedInstanceState==null){
            activeFragment = fragment1;
            createFragment(activeFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);

        String fra = getIntent().getStringExtra("fragment");
        if (fra != null){
            if(fra.equals("home")){
                activeFragment = fragment1;
                loadFragment(activeFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            } else if(fra.equals("matches")){
                activeFragment = fragment2;
                loadFragment(activeFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_matches);
            }else if(fra.equals("chats")){
                activeFragment = fragment3;
                loadFragment(activeFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_chats);
            }
        }
    }

    private void createFragment(Object fragment){
        if (fragment != null) {
            fm
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out
                )
                .add(R.id.fragment_container, (Fragment) fragment)
                .commit();
        }
    }

    private boolean loadFragment(Object fragment) {
        if (fragment != null) {
            fm
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out
                )
                .replace(R.id.fragment_container, (Fragment) fragment)
                .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                if (activeFragment != fragment1){
                    activeFragment = fragment1;
                    loadFragment(activeFragment);
                }
                break;
            case R.id.nav_matches:
                if (activeFragment != fragment2){
                    activeFragment = fragment2;
                    loadFragment(activeFragment);
                }
                break;
            case R.id.nav_chats:
                if (activeFragment != fragment3){
                    activeFragment = fragment3;
                    loadFragment(activeFragment);
                }
                break;
            case R.id.nav_profile:
                if (activeFragment != fragment4){
                    activeFragment = fragment4;
                    loadFragment(activeFragment);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void openBottomSheet(){
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}