package ph.edu.usc.jaidar.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {
    private final String profileUid;

    public ViewPageAdapter(@NonNull FragmentActivity fa, String profileUid) {
        super(fa);
        this.profileUid = profileUid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new AboutFragment();
        } else {
            fragment = new ReviewsFragment();
        }

        // Pass profileUid to the fragment via Bundle
        Bundle args = new Bundle();
        args.putString("profileUid", profileUid);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
