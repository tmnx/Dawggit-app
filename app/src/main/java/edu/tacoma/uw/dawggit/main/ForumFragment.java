/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.course.searchCourseActivity;
import edu.tacoma.uw.dawggit.forum.Forum;
import edu.tacoma.uw.dawggit.forum.ForumAddActivity;
import edu.tacoma.uw.dawggit.forum.ForumDb;
import edu.tacoma.uw.dawggit.forum.ForumDisplayActivity;
import edu.tacoma.uw.dawggit.forum.SearchReviewActivity;

/**
* Displays forum posts to a user.
 * @author Sean Smith
 * @version Sprint 1
 */
public class ForumFragment extends Fragment {
    private boolean mTwoPane;
    private List<Forum> mForumList;
    private RecyclerView mRecyclerView;
    private ForumDb mForumDB;


    /**
     *
     */
    public ForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Forum Fragment
     */
    public static ForumFragment newInstance() {
        ForumFragment fragment = new ForumFragment();
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
     * Chanes the behavior of on create view so we can add buttons
     * @param inflater Used to create a view
     * @param container View group of this object
     * @param savedInstanceState Saved state of the instance
     * @return View that has been created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ve = inflater.inflate(R.layout.fragment_forum, container, false);
        mRecyclerView = ve.findViewById(R.id.item_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        Button addButton = ve.findViewById(R.id.new_post);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch();
            }
        });

        Button searchButton = ve.findViewById(R.id.searchReviewButton);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTwo();
            }
        });
        return ve;
    }

    /**
     * Launch add course activity when user click add course button.
     */
    private void launchTwo() {
        Intent intent = new Intent(getActivity(), SearchReviewActivity.class);

        startActivity(intent);
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
            mForumList = null;
            if (mForumList == null) {
                new CoursesTask().execute(getString(R.string.get_threads));
            }
        }
        else {
            Toast.makeText(getActivity(),
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();

            if (mForumDB == null) {
                mForumDB = new ForumDb(getActivity());
            }
            if (mForumList == null) {
                mForumList = mForumDB.getThreads();
                setupRecyclerView(mRecyclerView);
                if(mRecyclerView != null) {
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                }

            }
        }
    }

    /**
     * Sets up the recycler view using the list of forums
     * @param recyclerView Recycle view that is being set up.
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if(mForumList != null) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter((HomeActivity) getActivity(), mForumList, mTwoPane));
        }
    }

    /**
     * Simple Recycle View class
     * @author Sean Smith
     * @version Sprint 1
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        /**
         * Parent of this activity
         */
        private final HomeActivity mParentActivity;
        /**
         * List of forum postings
         */
        private final List<Forum> mValues;
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
                Forum item = (Forum) view.getTag();
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
//                    Toast.makeText(context, "Course review clicked " + context.toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ForumDisplayActivity.class);
                    intent.putExtra(ForumDisplayActivity.ARG_ITEM_ID, item);
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(HomeActivity parent,
                                      List<Forum> items,
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
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.course_list_content, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Sets up the text and listeners
         * @param holder Used to reference each list item.
         * @param position Position of each list item.
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getTitle());
            holder.mContentView.setText(mValues.get(position).getDate());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Returns the number of items
         * @return Number of items
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * Class used to generate the list of forum posts
         * @author Sean Smith
         * @version Sprint1
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
         * Loads the forum fragment with new data when a post reuqest is exucted.
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
                    mForumList = Forum.parseCourseJson(
                            jsonObject.getString("names"));
                    if (mForumDB == null) {
                        mForumDB = new ForumDb(getActivity().getApplicationContext());
                    }

                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mForumDB.deleteCourses();

                    // Also, add to the local database
                    for (int i=0; i<mForumList.size(); i++) {
                        Forum forum = mForumList.get(i);
                        mForumDB.insertForum(forum.getThreadId(),
                                forum.getTitle(),
                                forum.getContent(),
                                forum.getDate(),
                                forum.getEmail());
                    }

                    if (!mForumList.isEmpty()) {
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
     * Launches a forum add activity
     */
    private void launch() {
        Intent intent = new Intent(getActivity(), ForumAddActivity.class);
        startActivity(intent);
    }

}
