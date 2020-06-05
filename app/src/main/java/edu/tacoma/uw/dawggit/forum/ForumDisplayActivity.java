package edu.tacoma.uw.dawggit.forum;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import edu.tacoma.uw.dawggit.comment.CommentAddActivity;
import edu.tacoma.uw.dawggit.comment.CommentsContent;

/**
 * Activity that displays a forum post
 * @author Sean Smith
 * @version Sprint1
 */
public class ForumDisplayActivity extends AppCompatActivity {
    /**
     * Item ID used for this activity.
     */
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * Thread ID for this forum
     */
    public String mThreadID;
    /**
     * Forum being displayed by this activity.
     */
    private Forum mForum;

    /**
     * JSON object of forum info
     */
    private JSONObject mJson;

    /**
     * Sets up this activity and initalizes all of the text boxes
     * @param savedInstanceState State required by parent class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_display);

        if (getIntent().getSerializableExtra(ARG_ITEM_ID) != null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mForum = (Forum) getIntent().getSerializableExtra(ARG_ITEM_ID);
        }
        if (mForum != null) {
            ((TextView) findViewById((R.id.page_title))).setText(mForum.getTitle());
            ((TextView) findViewById((R.id.page_date))).setText(mForum.getDate());
            ((TextView) findViewById((R.id.page_email))).setText(mForum.getEmail());
            ((TextView) findViewById((R.id.post_content))).setText(mForum.getContent());
        }

        ImageButton finishButton = findViewById(R.id.end);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences myPrefs = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        final String userEmail = myPrefs.getString(getString(R.string.USER_EMAIL), null);

        Button deleteButton = findViewById(R.id.post_delete);
        String email1 = userEmail.toLowerCase().trim();
        String email2 = mForum.getEmail().toLowerCase().trim();

        if(email1.equals(email2)) {
            deleteButton.setVisibility(View.VISIBLE);
            System.out.print(email1 + " " + email2);
        }
        else {
            deleteButton.setVisibility(View.INVISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(ForumDisplayActivity.this).create();
                alertDialog.setTitle("Are you sure you would like to delete this post");
                alertDialog.setMessage("You are about to delete this post");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteForum(mForum);
                        finish();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog.show();
            }
        });

        ImageButton emailButton =  findViewById(R.id.email_share);
        emailButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@uw.edu"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Dawggit Thread Posting \"" +mForum.getTitle() + "\"");
                i.putExtra(Intent.EXTRA_TEXT   , "I wanted to share this dawggit thread with you, it said this: \n" + mForum.getContent());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ForumDisplayActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button replyButton = (Button) findViewById(R.id.replyButton);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForumDisplayActivity.this,
                                        CommentAddActivity.class);
                i.putExtra("thread_id", mForum.getThreadId());
                startActivity(i);
            }
        }) ;

        Button viewRepButton = (Button) findViewById(R.id.viewRepliesButton);
        viewRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForumDisplayActivity.this,
                                        CommentsContent.class);
                i.putExtra("thread_id", mForum.getThreadId());
                startActivity(i);
            }
        });

    }

    /**
     * Adds a forum to the database using a post request
     * @param forum Forum being added to the database
     */
    public void deleteForum(Forum forum) {
        StringBuilder url = new StringBuilder(getString(R.string.delete_thread));
        mJson = new JSONObject();

        try {
            mJson.put(Forum.TID, forum.getThreadId());
            mJson.put(Forum.EMAIL, forum.getEmail());
            new removeThreadAsync().execute(url.toString());
        }
        catch(JSONException e) {
            Toast.makeText(this, "Error with JSON deletion:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Class that is used to get the list of threads from the database
     * @author Sean
     * @version Sprint1
     */
    private class removeThreadAsync extends AsyncTask<String, Void, String> {
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
                    wr.write(mJson.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to delete the forum, Reason: "
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
            if (s.startsWith("Unable to remove the post")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Post removed successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Post couldn't be removed: "
                                    + jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on removing post"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
            }
        }
    }
}
