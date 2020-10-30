package com.lysofts.luku;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lysofts.luku.fragments.ChatsFragment;
import com.lysofts.luku.fragments.HomeFragment;
import com.lysofts.luku.fragments.MatchesFragment;
import com.lysofts.luku.fragments.MoreFragment;
import com.lysofts.luku.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new HomeFragment());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    private boolean loadFragment(Object fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.slide_out
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