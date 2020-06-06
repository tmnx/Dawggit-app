/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.review;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.comment.Comment;
import edu.tacoma.uw.dawggit.comment.CommentAddActivity;

/**
 * Handles the add review feature. The user can review a course and
 * is able to view reviews.
 *
 * @author Minh Nguyen
 */
public class ReviewAddActivity extends AppCompatActivity {

    /**
     * String to specify the feature.
     */
    public static final String ADD_REVIEW = "ADD_REVIEW";

    /**
     * JSON objects of Reviews.
     */
    private JSONObject mReviewJSON;

    /**Used to get the Current user's Email*/
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_add);

        String courseID = getIntent().getStringExtra("course_id");

        mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        String userEmail = mSharedPreferences.getString(getString(R.string.USER_EMAIL), null);

        EditText commentText = findViewById(R.id.reviewText);

        Button commitReviewButton = findViewById(R.id.buttonCommitReview);
        commitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userEmail == null || TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ReviewAddActivity.this,
                            "Invalid Email, Please log out and log back in",
                            Toast.LENGTH_SHORT).show();
                    Log.e("ReviewAddActivity Email", "mSharedPreferences did not pass correct email");
                } else {
                    String email = userEmail;
                    String content = commentText.getText().toString();
                    Review review = new Review(email, courseID, content);

                    if (content.length() > 255) {
                        Toast.makeText(ReviewAddActivity.this,
                                "Reviews can only be 255 characters", Toast.LENGTH_SHORT).show();
                        Log.d("ReviewAddActivity", "Content is too long");
                    } else {
                        addReview(review);
                        finish();
                    }
                }
            }
        });

        ImageView finishButton = findViewById(R.id.closeCourseContent);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Add the user review to the backend database.
     *
     * @param review user review.
     */
    public void addReview(Review review) {
        StringBuilder url = new StringBuilder(getString(R.string.add_review));
        mReviewJSON = new JSONObject();

        try {
            mReviewJSON.put(Review.EMAIL, review.getEmail());
            mReviewJSON.put(Review.COURSE_CODE, review.getCourse_code());
            mReviewJSON.put(Review.CONTENT, review.getContent());
            new ReviewAddActivity.AddCourseAsyncTask().execute(url.toString());
        }
        catch(JSONException e) {
            Toast.makeText(this, "Error with JSON creation on adding a review:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Private helper class to sync the app with the backend database.
     */
    private class AddCourseAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(urlConnection.getOutputStream());

                    // For Debugging
                    Log.i(ADD_REVIEW, mReviewJSON.toString());
                    wr.write(mReviewJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add the new review, Reason: "
                            + e.getMessage();
                } finally {
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
            if (s.startsWith("Unable to add the new review")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Review Added successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Review couldn't be added: "
                                    + jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                    Log.e(ADD_REVIEW, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding post"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(ADD_REVIEW, e.getMessage());
            }
        }
    }
}
