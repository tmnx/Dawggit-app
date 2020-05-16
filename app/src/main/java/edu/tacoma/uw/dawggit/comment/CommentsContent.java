package edu.tacoma.uw.dawggit.comment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

/**
 * Displays the course content.
 *
 * @author Minh Nguyen
 */
public class CommentsContent extends AppCompatActivity {

    /**
     * A list of comments.
     */
    List<Comment> mCommentList;

    /**
     * Local comment database.
     */
    CommentDB mCommentDB;

    /**
     * The current thread the user is in.
     */
    String mThreadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_content);

        mThreadID = getIntent().getStringExtra("thread_id");

        ImageView closeButton = findViewById(R.id.close_comments);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Connect to db and refresh view.
     */
    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mCommentList == null) {
                new CoursesTask().execute(getString(R.string.get_comments) + mThreadID);
            }
        }
        else {
            Toast.makeText(this,
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();

            if (mCommentDB == null) {
                mCommentDB = new CommentDB(this);
            }
            if (mCommentList == null) {
                mCommentList = mCommentDB.getComments(mThreadID);
                setupList();

            }
        }
    }

    /**
     * Set up the list of Comments for the ListView.
     */
    void setupList() {
        if (!mCommentList.isEmpty() && mCommentList != null) {
            ListView listView = findViewById(R.id.comments_listview);
            ArrayList<String> list = new ArrayList<>();

            for (Comment c: mCommentList) {
                list.add(c.getDate() + "\n" + c.getEmail() + ": " + c.getContent());
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                                                android.R.layout.simple_list_item_1,
                                                list);

            listView.setAdapter(arrayAdapter);
        }
    }

    /**
     * Sync the list with the remote database. Get most updated comments.
     */
    private class CoursesTask extends AsyncTask<String, Void, String> {

       /**
        * Connect to remote database.
        * @param urls urls strings
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
         * Check if post is added successfully or not.
         * @param s JSON string.
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), "Unable to download" + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("success")) {
                    mCommentList = Comment.parseCommentJSON(
                            jsonObject.getString("names"));
                    if (mCommentDB == null) {
                        mCommentDB = new CommentDB(getApplicationContext());
                    }

                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mCommentDB.deleteAllComments();

                    // Also, add to the local database
                    for (int i = 0; i < mCommentList.size(); i++) {
                        Comment comment = mCommentList.get(i);
                        mCommentDB.insertComment(comment.getEmail(),
                                comment.getThread_id(),
                                comment.getDate(),
                                comment.getContent());
                    }

                    if (!mCommentList.isEmpty()) {
                        setupList();
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
