package com.spiraclestudios.autoskola;

/**
 * Created by benji on 14/10/2015.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TestActivityPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TestActivityPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TestActivityFragment();
            case 1:
                return new TestActivityFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}