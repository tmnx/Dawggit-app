package edu.tacoma.uw.dawggit.review;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import edu.tacoma.uw.dawggit.comment.Comment;
import edu.tacoma.uw.dawggit.comment.CommentDB;
import edu.tacoma.uw.dawggit.comment.CommentsContent;

/**
 * Displays the reviews content.
 *
 * @author Minh Nguyen
 */
public class ReviewsContent extends AppCompatActivity {

    /**
     * A list of reviews.
     */
    List<Review> mReviewList;

    /**
     * Local comment database.
     */
    ReviewDB mReviewDB;

    /**
     * The current thread the user is in.
     */
    String mCourseID;

    /**
     *
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_content);

        mCourseID = getIntent().getStringExtra("course_id");

        ImageView finishButton = findViewById(R.id.closeReviewsList);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Refresh reviews content.
     */
    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mReviewList == null) {
                new ReviewsContent.CoursesTask().execute(getString(R.string.get_reviews) + mCourseID);
            }
        }
        else {
            Toast.makeText(this,
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();

            if (mReviewDB == null) {
                mReviewDB = new ReviewDB(this);
            }
            if (mReviewList == null) {
                mReviewList = mReviewDB.getReviews(mCourseID);
                setupReviewList();
            }
        }
    }

    /**
     * Set up the list of Reviews for the ListView.
     */
    void setupReviewList() {
        if (!mReviewList.isEmpty() && mReviewList != null) {
            ListView listView = findViewById(R.id.reviews_listview);
            ArrayList<String> list = new ArrayList<>();

            for (Review r: mReviewList) {
                list.add(r.getDate().subSequence(0,10) + "\n" + r.getEmail() + "\n\n" + r.getContent());
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                                                        android.R.layout.simple_list_item_1,
                                                        list);

            listView.setAdapter(arrayAdapter);
        }
    }

    /**
     * Sync the list with the remote database. Get most updated reviews.
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
                    response = "Unable to download the list of reviews, Reason: "
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
         * Check if the connection to the database is a success and if JSON parameter is valid.
         *
         * @param s is the JSON string
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
                    mReviewList = Review.parseReviewJSON(
                            jsonObject.getString("names"));
                    if (mReviewDB == null) {
                        mReviewDB = new ReviewDB(getApplicationContext());
                    }

                    // Delete old data so that you can refresh the local
                    // database with the network data.
                    mReviewDB.deleteAllReviews();

                    // Also, add to the local database
                    for (int i = 0; i < mReviewList.size(); i++) {
                        Review review = mReviewList.get(i);
                        mReviewDB.insertReview(review.getEmail(),
                                review.getCourse_code(),
                                review.getDate(),
                                review.getContent());
                    }

                    if (!mReviewList.isEmpty()) {
                        setupReviewList();
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
