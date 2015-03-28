package com.reader.freshmanapp.mywallet.adapter;

/**
 * Created by Ramkumar on 28/03/15.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.reader.freshmanapp.mywallet.DemoFragment;

public class DemoPagerAdapter extends FragmentPagerAdapter {

    public static final int pagerCount = 7;


    public DemoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int i) {
        return DemoFragment.newInstance(i);
    }

    @Override public int getCount() {
        return pagerCount;
    }
}