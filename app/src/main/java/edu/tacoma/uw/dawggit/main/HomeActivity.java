/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import edu.tacoma.uw.dawggit.main.SectionsPagerAdapter;

import android.os.Bundle;
import edu.tacoma.uw.dawggit.R;

/**
 * Parent for tabs/tab fragments
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }
}
