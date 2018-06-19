package com.team.socero.loopvendor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahrus Kazi on 2018-06-18.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> mFragments;

    public MainFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();
        mFragments.add(new DashboardFragment());
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mFragments.get(0);
        }
        else{
            return null;
        }

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 1;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Dashboard";
            default:
                return null;
        }
    }
}
