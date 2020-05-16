package edu.tacoma.uw.dawggit.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.course.Course;
import edu.tacoma.uw.dawggit.course.CourseAddActivity;
import edu.tacoma.uw.dawggit.course.CourseDB;
import edu.tacoma.uw.dawggit.course.CourseDisplayActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseReviewFragment extends Fragment {
    private boolean mTwoPane;
    private List<Course> mCourseList;
    private RecyclerView mRecyclerView;
    private CourseDB mCourseDB;

    public CourseReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */
    public static CourseReviewFragment newInstance() {
        CourseReviewFragment fragment = new CourseReviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Calls parent onCreate.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    /**
     * Set up the layout and hook up buttons with listen.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the new view for app.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ve = inflater.inflate(R.layout.fragment_course_review, container, false);
        mRecyclerView = ve.findViewById(R.id.course_review_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        Button addButton = ve.findViewById(R.id.addReviewButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch();
            }
        });
        return ve;
    }

    /**
     * On resume refresh course list.
     */
    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mCourseList = null;
            if (mCourseList == null) {
                new CoursesTask().execute(getString(R.string.get_courses));
            }
        }
        else {
            Toast.makeText(getActivity(),
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();

            if (mCourseDB == null) {
                mCourseDB = new CourseDB(getActivity());
            }
            if (mCourseList == null) {
                mCourseList = mCourseDB.getCourses();
                setupRecyclerView(mRecyclerView);
                if(mRecyclerView != null) {
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                }
            }
        }
    }


    /**
     * Set up the recycler view to display a list of courses.
     * @param recyclerView a recycler view to be added to
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if(mCourseList != null) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter((HomeActivity) getActivity(),
                                                                        mCourseList, mTwoPane));
        }
    }

    /**
     * Helper class to manager the recycler view
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final HomeActivity mParentActivity;
        private final List<Course> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course item = (Course) view.getTag();
                if (mTwoPane) {

//                    Bundle arguments = new Bundle();
//                    arguments.putSerializable(CourseDetailFragment.ARG_ITEM_ID, item);
//                    CourseDetailFragment fragment = new CourseDetailFragment();
//                    fragment.setArguments(arguments);
//                    mParentActivity.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.item_detail_container, fragment)
//                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, CourseDisplayActivity.class);
                    intent.putExtra(CourseDisplayActivity.ARG_ITEM_ID, item);
                    context.startActivity(intent);
                }
            }
        };

        /**
         * Initializes fields.
         *
         * @param parent parent activity
         * @param items list of courses
         * @param twoPane boolean value
         */
        SimpleItemRecyclerViewAdapter(HomeActivity parent,
                                      List<Course> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        /**
         * Create view holder for course.
         * @param parent ViewGroup
         * @param viewType view type
         * @return the view holder of course.
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.course_list_content, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Bind to view holder.
         * @param holder view holder.
         * @param position which course.
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getCourse_code());
            holder.mContentView.setText(mValues.get(position).getTitle());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Get item count
         * @return return item counts
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * Helper class to hold a view (for each course item).
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    /**
     * Helper class to manage remote DB connection and retrieve DB.
     */
    private class CoursesTask extends AsyncTask<String, Void, String> {

        /**
         * Check if response can connect to DB.
         * @param urls
         * @return response
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of posts, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;

        }

        /**
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to download" + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("success")) {
                    mCourseList = Course.parseCourseJSON(
                            jsonObject.getString("names"));
                    if (mCourseDB == null) {
                        mCourseDB = new CourseDB(getActivity().getApplicationContext());
                    }

                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mCourseDB.deleteCourses();

                    // Also, add to the local database
                    for (int i = 0; i< mCourseList.size(); i++) {
                        Course course = mCourseList.get(i);
                        mCourseDB.insertCourse(course.getCourse_code(),
                                course.getTitle(),
                                course.getCourse_info(),
                                course.getEmail());
                    }

                    if (!mCourseList.isEmpty()) {
                        setupRecyclerView((RecyclerView) mRecyclerView);
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Launch add course activity when user click add course button.
     */
    private void launch() {
        Intent intent = new Intent(getActivity(), CourseAddActivity.class);
        startActivity(intent);
    }

}
