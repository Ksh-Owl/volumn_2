package com.example.volumn.rank;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new RankWeekFragment();
            case 1:
                return new RankmonthFragment();
            case 2:
                return new FriendsListFragment();

            default:
        }
        return new FriendsListFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
