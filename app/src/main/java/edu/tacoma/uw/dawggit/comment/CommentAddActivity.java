package edu.tacoma.uw.dawggit.comment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import edu.tacoma.uw.dawggit.course.CourseAddActivity;
import edu.tacoma.uw.dawggit.forum.Forum;
import edu.tacoma.uw.dawggit.forum.ForumAddActivity;

/**
 * Handles the add comment feature. The user can reply to a forum and
 * is able to view replies.
 *
 * @author Minh Nguyen
 */
public class CommentAddActivity extends AppCompatActivity {

    /**
     * String to specify the feature.
     */
    public static final String ADD_COMMENT = "ADD_COMMENT";

    /**Used to get the Current user's Email*/
    private SharedPreferences mSharedPreferences;

    /**
     * JSON objects of Comments.
     */
    private JSONObject mCommentJSON;

    /**
     * Create view and setup buttons / text views.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_add);

        ImageView finishButton = findViewById(R.id.closeAddComment);
        final EditText commentText = findViewById(R.id.commentBox);
        Button addCommentButton = findViewById(R.id.new_commentButton);

        Intent i = this.getIntent();
        final String thread_id = i.getStringExtra("thread_id");

        mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        String userEmail = mSharedPreferences.getString(getString(R.string.USER_EMAIL), null);
//        Log.e("Testing thread id", thread_id);  // for debugging

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userEmail == null || TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(CommentAddActivity.this,
                            "Invalid Email, Please log out and log back in",
                            Toast.LENGTH_SHORT).show();
                    Log.e("ForumAddActivity Email", "mSharedPreferences did not pass correct email");
                }
                String email = userEmail;
                String content = commentText.getText().toString();
                Comment comment = new Comment(email, thread_id, content);
                if(content.length() > 255) {
                    Toast.makeText(CommentAddActivity.this,
                            "Comments can only be 255 characters", Toast.LENGTH_SHORT).show();
                    Log.d("CommentAddActivity", "Content is too long");
                }
                else {
                    addComment(comment);
                    finish();
                }

            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Add the user comment to the backend database.
     *
     * @param comment user comment.
     */
    public void addComment(Comment comment) {
        StringBuilder url = new StringBuilder(getString(R.string.add_comment));
        mCommentJSON = new JSONObject();

        try {
            mCommentJSON.put(Comment.EMAIL, comment.getEmail());
            mCommentJSON.put(Comment.THREAD_ID, comment.getThread_id());
            mCommentJSON.put(Comment.CONTENT, comment.getContent());
            new CommentAddActivity.AddCourseAsyncTask().execute(url.toString());
        }
        catch(JSONException e) {
            Toast.makeText(this, "Error with JSON creation on adding a course:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Private helper class to sync the app with the backend database.
     */
    private class AddCourseAsyncTask extends AsyncTask<String, Void, String> {
        
       /**
        * Connect to remote database.
        */
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
                    Log.i(ADD_COMMENT, mCommentJSON.toString());
                    wr.write(mCommentJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add the new course, Reason: "
                            + e.getMessage();
                } finally {
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
            if (s.startsWith("Unable to add the new post")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Post Added successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Post couldn't be added: "
                                    + jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                    Log.e(ADD_COMMENT, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding post"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(ADD_COMMENT, e.getMessage());
            }
        }
    }
}
