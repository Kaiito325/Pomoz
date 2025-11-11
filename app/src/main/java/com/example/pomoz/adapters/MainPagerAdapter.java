package com.example.pomoz.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pomoz.fragments.HomeFragment;
import com.example.pomoz.fragments.AddFragment;
import com.example.pomoz.fragments.AccountFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new AddFragment();
            case 2: return new AccountFragment();
        }
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
