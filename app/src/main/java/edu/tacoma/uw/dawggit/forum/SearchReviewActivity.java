/**
 * TCSS 450 Mobile Application
 * Team 6
 **/
package edu.tacoma.uw.dawggit.forum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.course.Course;
import edu.tacoma.uw.dawggit.course.CourseDB;
import edu.tacoma.uw.dawggit.main.CourseReviewFragment;
import edu.tacoma.uw.dawggit.review.ReviewsContent;

/**
 * Allows user to filter the course reviews to find the courses they want.
 */
public class SearchReviewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    private List<Forum> mForumList;
    private RecyclerView mRecyclerView;
    private ForumDb mForumDB;
    public static final String ARG_ITEM_ID = "course_item_id";
    private Forum mForum;

    /**
     * Initialize view and setup listeners for buttons.
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_review);


        mForumDB = new ForumDb(this);

        mForumList = mForumDB.getThreads();







        ImageView closeButton = findViewById(R.id.closeSearch);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (mForumList != null) {
            recyclerView = findViewById(R.id.recyclerView);
            recyclerAdapter = new RecyclerAdapter(mForumList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(recyclerAdapter);

            // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            // recyclerView.addItemDecoration(dividerItemDecoration);
        }











    }







    /**
     * Refresh with the most updated comments.
     */
    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mForumList = null;
            if (mForumList == null) {
                new CoursesTask().execute(getString(R.string.get_courses));
            }
        }
        else {


            if (mForumDB == null) {
                mForumDB = new ForumDb(this);
            }
            if (mForumList == null) {
                mForumList = mForumDB.getThreads();
                //setupRecyclerView(mRecyclerView);

            }
        }
    }

    /**
     *  Sets up the search bar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.course_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Helper class to manage remote DB connection and retrieve DB.
     */
    private class CoursesTask extends AsyncTask<String, Void, String> {

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

        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to")) {

                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("success")) {
                    mForumList = Forum.parseCourseJson(
                            jsonObject.getString("names"));
                    if (mForumDB == null) {
                        mForumDB = new ForumDb(getApplicationContext());
                    }

                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mForumDB.deleteCourses();

                    // Also, add to the local database
                    for (int i = 0; i< mForumList.size(); i++) {
                        Forum forum = mForumList.get(i);
                        mForumDB.insertForum(forum.getThreadId(),
                                forum.getTitle(),
                                forum.getContent(),
                                forum.getDate(),
                                forum.getEmail());
                    }

                    if (!mForumList.isEmpty()) {

                    }
                }

            } catch (JSONException e) {

            }
        }

    }
}
