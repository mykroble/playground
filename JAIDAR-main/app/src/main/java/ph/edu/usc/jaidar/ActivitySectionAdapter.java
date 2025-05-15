package ph.edu.usc.jaidar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ActivitySectionAdapter extends FragmentStateAdapter {
    private final FragmentActivity fa;

    public ActivitySectionAdapter(@NonNull FragmentActivity fa) {
        super(fa);
        this.fa = fa;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new OffersFragment(fa);    //ill make this later
        } else {
            fragment = new YourPostsFragment(fa);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
