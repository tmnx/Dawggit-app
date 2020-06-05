package edu.tacoma.uw.dawggit.forum;

import androidx.appcompat.app.AppCompatActivity;
import edu.tacoma.uw.dawggit.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for adding a forum post to the database
 * @author Sean Smith
 * @version Sprint1
 */
public class ForumAddActivity extends AppCompatActivity {
    /**
     * String to reference this activity
     */
    public static final String ADD_FORUM = "ADD_FORUM";
    /**
     * OBject containing list of forums as JSON
     */
    private JSONObject mForumJSON;
    private SharedPreferences mSharedPreferences;
    @Override
    /**
     * Initializes the properties for all of the buttons and text.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_add);

        mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        final String userEmail = mSharedPreferences.getString(getString(R.string.USER_EMAIL), null);
        Button addButton = findViewById(R.id.post);
        ImageButton finishButton = findViewById(R.id.finish);
        final EditText title = findViewById(R.id.post_title);
        final EditText text = findViewById(R.id.forum_body);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String threadTitle = title.getText().toString();
                String threadContent = text.getText().toString();
                Forum forum = new Forum(threadTitle, threadContent, userEmail);
                if(userEmail == null || TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ForumAddActivity.this,
                            "Invalid Email, Please log out and log back in",
                            Toast.LENGTH_SHORT).show();
                    Log.e("ForumAddActivity Email", "mSharedPreferences did not pass correct email");
                }
                else if(threadTitle.length() > 255) {
                    Toast.makeText(ForumAddActivity.this,
                            "Title can only have 255 characters", Toast.LENGTH_SHORT).show();
                    Log.d("ForumAddActivity", "Title is too long");
                }
                else if(threadContent.length() > 255) {
                    Toast.makeText(ForumAddActivity.this,
                            "Content can only have 255 characters", Toast.LENGTH_SHORT).show();
                    Log.d("ForumAddActivity", "Content is too long");
                }
                else if(threadTitle.trim().length() < 1) {
                    Toast.makeText(ForumAddActivity.this,
                            "Thread Title must contain something", Toast.LENGTH_SHORT).show();
                    Log.d("ForumAddActivity", "Empty Thread Title");
                }
                else if(threadContent.trim().length() < 1) {
                    Toast.makeText(ForumAddActivity.this,
                            "Thread Content must contain something", Toast.LENGTH_SHORT).show();
                    Log.d("ForumAddActivity", "Empty Content");
                }
                else {
                    addForum(forum);
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
     * Adds a forum to the database using a post request
     * @param forum Forum being added to the database
     */
    public void addForum(Forum forum) {
        StringBuilder url = new StringBuilder(getString(R.string.add_thread));
        mForumJSON = new JSONObject();

        try {
            mForumJSON.put(Forum.TITLE, forum.getTitle());
            mForumJSON.put(Forum.CONTENT, forum.getContent());
            mForumJSON.put(Forum.EMAIL, forum.getEmail());
            new AddCourseAsyncTask().execute(url.toString());
        }
        catch(JSONException e) {
            Toast.makeText(this, "Error with JSON creation on adding a course:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Class that is used to get the list of threads from the database
     * @author Sean
     * @version Sprint1
     */
    private class AddCourseAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        /**
         * Trys to poll the database and get information as json
         */
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
                    Log.i(ADD_FORUM, mForumJSON.toString());
                    wr.write(mForumJSON.toString());
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
         * Attempts to create a json object if it false a toast is displayed
         * @param s String returned by get request used to create json object.
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
                    Log.e(ADD_FORUM, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding post"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(ADD_FORUM, e.getMessage());
            }
        }
    }
}
