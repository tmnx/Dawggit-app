package edu.tacoma.uw.dawggit.course;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import edu.tacoma.uw.dawggit.forum.ForumDisplayActivity;
import edu.tacoma.uw.dawggit.review.ReviewAddActivity;
import edu.tacoma.uw.dawggit.review.ReviewsContent;

/**
 * Display the course info and its features:
 *  - delete course
 *  - view reviews
 *  - add review
 *
 *  @author Minh Nguyen
 * @version Sprint 2
 */
public class CourseDisplayActivity extends AppCompatActivity {

    /**
     * Item ID used for this activity.
     */
    public static final String ARG_ITEM_ID = "course_item_id";

    /**
     * Forum being displayed by this activity.
     */
    private Course mCourse;

    /**
     * JSON object of course info
     */
    private JSONObject mJSON;

    /**
     * Set up the buttons listeners and functions.
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_display);

        if (getIntent().getSerializableExtra(ARG_ITEM_ID) != null) {
            // to load content from a content provider.
            mCourse = (Course) getIntent().getSerializableExtra(ARG_ITEM_ID);
        }
        if (mCourse != null) {
            ((TextView) findViewById((R.id.textViewCourseID))).setText(mCourse.getCourse_code());
            ((TextView) findViewById((R.id.textViewCourseTitle))).setText(mCourse.getTitle());
            ((TextView) findViewById((R.id.textViewCourseContent))).setText(mCourse.getCourse_info());
        }

        ImageView closeButton = findViewById(R.id.closeCourseView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button addReviewButton = findViewById(R.id.buttonAddReview);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                                        ReviewAddActivity.class);
                i.putExtra("course_id", mCourse.getCourse_code());
                startActivity(i);
            }
        });

        Button viewRepButton = (Button) findViewById(R.id.buttonViewReviews);
        viewRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CourseDisplayActivity.this,
                                        ReviewsContent.class);
                i.putExtra("course_id", mCourse.getCourse_code());
                startActivity(i);
            }
        });

        // Get user email to compare to the user that created the course
        SharedPreferences myPrefs = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        final String userEmail = myPrefs.getString(getString(R.string.USER_EMAIL), null);

        String email1 = userEmail.toLowerCase().trim();
        String email2 = mCourse.getEmail().toLowerCase().trim();

        ImageView deleteCourseButton = findViewById(R.id.buttonDeleteCourse);

        // if user is the one that created the course, user can delete the course
        if(email1.equals(email2)) {
            deleteCourseButton.setVisibility(View.VISIBLE);
            System.out.print(email1 + " " + email2);            // for testing
        }
        else {
            deleteCourseButton.setVisibility(View.INVISIBLE);
        }

        deleteCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(CourseDisplayActivity.this).create();
                alertDialog.setTitle("Are you sure you want to delete this course?");
                alertDialog.setMessage("This cannot be undo");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCourse(mCourse);
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
    }

    /**
     * Delete a course from the database using a post request.
     * @param course is a course being added to the database
     */
    public void deleteCourse(Course course) {
        StringBuilder url = new StringBuilder(getString(R.string.delete_course));
        mJSON = new JSONObject();

        try {
            mJSON.put(Course.COURSEID, course.getCourse_code());
            mJSON.put(Course.EMAIL, course.getEmail());
            new removeCourseAsync().execute(url.toString());
        }
        catch(JSONException e) {
            Toast.makeText(this, "Error with deletion:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Class that is used to get the list of courses from the database
     *
     * @author Minh Nguyen
     * @version Sprint 2
     */
    private class removeCourseAsync extends AsyncTask<String, Void, String> {

        @Override
        /**
         * Try to poll the database and get information as json
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
                    wr.write(mJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add the new course. Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Attempts to create a json object if it false a toast is displayed.
         *
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
                    Toast.makeText(getApplicationContext(), "Course removed successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Course couldn't be removed: "
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
