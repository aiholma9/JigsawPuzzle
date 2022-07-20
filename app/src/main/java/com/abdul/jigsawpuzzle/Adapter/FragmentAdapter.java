package com.abdul.jigsawpuzzle.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.abdul.jigsawpuzzle.Fragment.CategoryFragment;
import com.abdul.jigsawpuzzle.Fragment.LastCreatedFragment;
import com.abdul.jigsawpuzzle.Fragment.MostPlayedFragment;

public class FragmentAdapter extends FragmentPagerAdapter{
    private Context context;

    public FragmentAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    //Position fragments in the viewPager
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return CategoryFragment.getInstance();

        else if (position == 1)
            return LastCreatedFragment.getInstance();

        else if (position == 2)
            return MostPlayedFragment.getInstance();

        else
            return null;
    }

    @Override//the total number fragments
    public int getCount() {
        return 3;
    }

    //Name fragments in the viewPager
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Category";

            case 1:
                return "Last Created";

            case 2:
                return "Most Played";
        }

        return "";
    }
}
