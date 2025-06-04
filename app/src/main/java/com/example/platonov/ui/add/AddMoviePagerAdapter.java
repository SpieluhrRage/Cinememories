package com.example.platonov.ui.add;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * PagerAdapter для ViewPager2: 2 вкладки — Manual и Search.
 */
public class AddMoviePagerAdapter extends FragmentStateAdapter {

    public AddMoviePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // position: 0 → AddManualFragment, 1 → AddSearchFragment
        if (position == 0) {
            return new AddManualFragment();
        } else {
            return new AddSearchFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}