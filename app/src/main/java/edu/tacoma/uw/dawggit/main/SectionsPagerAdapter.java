/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.tacoma.uw.dawggit.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * @author Sean Smith
 * @version Sprint1
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    /**
     * Titles for each tab
     */
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_5};
    /**
     * Required context of this object.
     */
    private final Context mContext;

    SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

    }

    /**
     * Returns the fragment for each position
     * @param position Tab that is being generated
     * @return A new instance of a fragment
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0) {
            return HomeFragment.newInstance();
        }
        else if(position == 1) {
            return ForumFragment.newInstance();
        }
        else if(position == 2) {
            return ListingsFragment.newInstance();
        }
        else if(position == 3) {
            //Reviews here
            return CourseReviewFragment.newInstance();
        }
        else if(position == 4) {
            return SettingFragment.newInstance();

        }
        return HomeFragment.newInstance();
    }

    /**
     * Returns name of each tab
     * @param position Position of tab being requested.
     * @return Name of tab being requested.
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     * Returns count of all tabs
     * @return Number of tabs.
     */
    @Override
    public int getCount() {
        // Show 5 total pages.
        return 5;
    }
}