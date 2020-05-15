package edu.tacoma.uw.dawggit.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.tacoma.uw.dawggit.R;

/**
 * Fragment Home page for the application
 * @version Sprint 1
 * @author Sean Smith
 */
public class HomeFragment extends Fragment {
    /**
     *
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     *
     * @param inflater Required object to generate a view.
     * @param container Required object to generate a view.
     * @param savedInstanceState Used to save the state.
     * @return Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_page_fragment, container, false);
//        final EditText home_text = v.findViewById(R.id.home1);
//        final EditText home_text2 = v.findViewById(R.id.home2);
//        home_text.setText(R.string.home_text);
//        home_text2.setText(R.string.home_text_2);
        return v;

    }
}
