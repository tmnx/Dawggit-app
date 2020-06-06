package edu.tacoma.uw.dawggit.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.course.Course;
import edu.tacoma.uw.dawggit.course.CourseAddActivity;
import edu.tacoma.uw.dawggit.course.CourseDB;
import edu.tacoma.uw.dawggit.course.CourseDisplayActivity;

/**
 * Displays course review posts to a user.
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Minh Nguyen
 * @version Sprint 2
 */
public class CourseReviewFragment extends Fragment {

    private boolean mTwoPane;
    private List<Course> mCourseList;
    private RecyclerView mRecyclerView;
    private CourseDB mCoursemDB;

    public CourseReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CourseReviewFragment.
     */
    public static CourseReviewFragment newInstance() {
        CourseReviewFragment fragment = new CourseReviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Used to change the behavior of the base on create
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    /**
     * Changes the behavior of on create view so we can add buttons
     * @param inflater Used to create a view
     * @param container View group of this object
     * @param savedInstanceState Saved state of the instance
     * @return View that has been created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ve = inflater.inflate(R.layout.fragment_course_review, container, false);

        mRecyclerView = ve.findViewById(R.id.course_item_list);
        assert mRecyclerView != null;

        setupCourseRecyclerView((RecyclerView) mRecyclerView);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                                        DividerItemDecoration.VERTICAL));

        Button addButton = ve.findViewById(R.id.new_course);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch();
            }
        });

        return ve;
    }

    /**
     * Overrides on resume so the database is queried whenever something is added
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

            if (mCoursemDB == null) {
                mCoursemDB = new CourseDB(getActivity());
            }
            if (mCourseList == null) {
                mCourseList = mCoursemDB.getCourses();
                setupCourseRecyclerView(mRecyclerView);
                if(mRecyclerView != null) {
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                                                    DividerItemDecoration.VERTICAL));
                }

            }
        }
    }

    /**
     * Launches a course add activity
     */
    private void launch() {
        Intent intent = new Intent(getActivity(), CourseAddActivity.class);
        startActivity(intent);
    }

    /**
     * Sets up the recycler view using the list of courses
     * @param recyclerView Recycle view that is being set up.
     */
    private void setupCourseRecyclerView(@NonNull RecyclerView recyclerView) {
        if(mCourseList != null) {
            recyclerView.setAdapter(new SimpleCourseRecyclerViewAdapter((HomeActivity) getActivity(),
                    mCourseList, mTwoPane));
        }
    }

    /**
     * Simple Recycle View class
     * @author Minh Nguyen
     * @version Sprint 2
     */
    public static class SimpleCourseRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleCourseRecyclerViewAdapter.CourseViewHolder> {

        /**
         * Parent of this activity
         */
        private final HomeActivity mParentActivity;

        /**
         * List of course postings
         */
        private final List<Course> mValues;

        /**
         * Whether the screen is two pane or not
         */
        private final boolean mTwoPane;

        /**
         * Sets up the listener for each forum item
         */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course item = (Course) view.getTag();
                if (mTwoPane) {
                } else {
                    Context context = view.getContext();
                    // testing
//                    Toast.makeText(context, "Course review clicked " + item.getCourse_code(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, CourseDisplayActivity.class);
                    intent.putExtra(CourseDisplayActivity.ARG_ITEM_ID, item);
                    context.startActivity(intent);
                }
            }
        };

        SimpleCourseRecyclerViewAdapter(HomeActivity parent,
                                        List<Course> items,
                                        boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        /**
         * Generates a view holder
         * @param parent Parent of the view holder
         * @param viewType Type of view.
         * @return Created view holder
         */
        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_list_content, parent, false);
            return new CourseViewHolder(view);
        }

        /**
         * Sets up the text and listeners
         * @param holder Used to reference each list item.
         * @param position Position of each list item.
         */
        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getCourse_code());
            holder.mContentView.setText(mValues.get(position).getTitle());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Returns the number of courses
         * @return Number of courses
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * Class used to generate the list of course posts
         * @author Minh Nguyen
         * @version Sprint 2
         */
        class CourseViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            CourseViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_course);
                mContentView = (TextView) view.findViewById(R.id.id_content);
            }
        }

    }
    /**
     * Asynchronous class used to contact the web to perform a get request
     */
    private class CoursesTask extends AsyncTask<String, Void, String> {

        /**
         * Attempts to connect to the database via a get request then returns a string containing JSON
         * @param urls URL the of the database
         * @return JSON retrieved from the database.
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
                    response = "Unable to download the list of courses, Reason: "
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
         * Loads the course fragment with new data when a post request is executed.
         * @param s String that is being converted to JSOn to generate the posts.
         */
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to download" + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("success")) {
                    mCourseList = Course.parseCourseReviewJSON(
                            jsonObject.getString("names"));
                    if (mCoursemDB == null) {
                        mCoursemDB = new CourseDB(getActivity().getApplicationContext());
                    }

                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mCoursemDB.deleteCourses();

                    // Also, add to the local database
                    for (int i=0; i<mCourseList.size(); i++) {
                        Course course = mCourseList.get(i);
                        mCoursemDB.insertCourse(course.getCourse_code(),
                                                course.getTitle(),
                                                course.getCourse_info(),
                                                course.getEmail());
                    }

                    if (!mCourseList.isEmpty()) {
                        setupCourseRecyclerView((RecyclerView) mRecyclerView);
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
