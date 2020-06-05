package edu.tacoma.uw.dawggit.course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Allows user to add another course to the courses to be reviewed.
 */
public class CourseAddActivity extends AppCompatActivity {

    public static final String ADD_COURSE = "ADD COURSE";

    /**
     * Course JSON object.
     */
    private JSONObject mCourseJSON;

    /**
     * Initialize view and setup listeners for buttons.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add);

        ImageView closeButton = findViewById(R.id.closeAddCourse);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // close add activity
            }
        });

        // Get user inputs
        final EditText CIDtext = findViewById(R.id.editCourseID);
        final EditText titleText = findViewById(R.id.editCourseTitle);
        final EditText infoText = findViewById(R.id.editCourseInfo);



        // Add course to database
        Button addButton = findViewById(R.id.addCourse);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course_code = CIDtext.getText().toString();
                String title = titleText.getText().toString();
                String info = infoText.getText().toString();

                // Get user email to compare to the user that created the course
                SharedPreferences myPrefs = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
                final String userEmail = myPrefs.getString(getString(R.string.USER_EMAIL), null);

                Course course = new Course(course_code, title, info, userEmail);
                addCourse(course);
//                finish();
            }
        });
    }

    /**
     * Add course to remote database.
     *
     * @param course the course to be added.
     */
    public void addCourse(Course course) {
        StringBuilder url = new StringBuilder(getString(R.string.add_course));
        mCourseJSON = new JSONObject();

        try {
            mCourseJSON.put(Course.COURSEID, course.getCourse_code());
            mCourseJSON.put(Course.TITLE, course.getTitle());
            mCourseJSON.put(Course.INFO, course.getCourse_info());
            mCourseJSON.put(Course.EMAIL, course.getEmail());
            new AddCourseAsyncTask().execute(url.toString());
        }
        catch(JSONException e) {
            Toast.makeText(this, "Error with JSON creation on adding a course:"
                    + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper class to sync the app with the remote database.
     */
    private class AddCourseAsyncTask extends AsyncTask<String, Void, String> {

        /**
         * Connect to database.
         *
         * @param urls urls string
         * @return return whether response to database is a success.
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
                    Log.i(ADD_COURSE, mCourseJSON.toString());
                    wr.write(mCourseJSON.toString());
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
         * Attempts to create a json object if it false a toast is displayed.
         * @param s JSON string.
         */
        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to add the new Course")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Course added successfully"
                            , Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Course couldn't be added"
                            , Toast.LENGTH_LONG).show();
                    Log.e(ADD_COURSE, jsonObject.getString("error"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error on Adding post"
                                + e.getMessage()
                        , Toast.LENGTH_LONG).show();
                Log.e(ADD_COURSE, e.getMessage());
            }
        }
    }
}
