package com.lysofts.luku;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.lysofts.luku.home_fragments.ChatsFragment;
import com.lysofts.luku.home_fragments.HomeFragment;
import com.lysofts.luku.home_fragments.MatchesFragment;
import com.lysofts.luku.home_fragments.MoreFragment;
import com.lysofts.luku.home_fragments.ProfileFragment;
import com.lysofts.luku.services.NotificationService;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState==null){
            createFragment(new HomeFragment());
        }

        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void createFragment(Object fragment){
        if (fragment != null) {
            getSupportFragmentManager()
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
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
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
        Object fragment = null;
        switch (item.getItemId()){
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_matches:
                fragment = new MatchesFragment();
                break;
            case R.id.nav_chats:
                fragment = new ChatsFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_more:
                fragment = new MoreFragment();
                break;
        }
        return loadFragment(fragment);
    }
}