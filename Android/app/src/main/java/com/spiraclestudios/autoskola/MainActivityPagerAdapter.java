package com.spiraclestudios.autoskola;

/**
 * Created by benji on 14/10/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.spiraclestudios.autoskola.Fragments.MainActivityFragment;

public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainActivityPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return MainActivityFragment.newInstance(Helper.Groups.AB);
            case 1:
                return MainActivityFragment.newInstance(Helper.Groups.CDT);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}