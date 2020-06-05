package edu.tacoma.uw.dawggit.course;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create a Course object that holds the information needed to add Course to remote
 * database.
 *
 * @author Minh Nguyen
 */
public class Course implements Serializable {

    /**
     * Course code
     */
    private String course_code;

    /**
     * Title of the course
     */
    private String title;

    /**
     * Course content
     */
    private String course_info;

    /**
     * User email who created the course
     */
    private String email;

    /**
     * Course code in sql
     */
    public static final String COURSEID = "course_code";

    /**
     * Course Title in sql
     */
    public static final String TITLE = "title";

    /**
     * Course content in sql
     */
    public static final String INFO = "course_info";

    /**
     * User email in sql
     */
    public static final String EMAIL = "email";

    /**
     * Create a course.
     *
     * @param theCID course code
     * @param theTitle course title
     * @param theInfo course information
     * @param theEmail user email that created the course review
     */
    public Course(String theCID, String theTitle, String theInfo, String theEmail) {
        course_code = theCID;
        title = theTitle;
        course_info = theInfo;
        email = theEmail;
    }

    // GETTERS
    public String getCourse_code() {
        return course_code;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse_info() {
        return course_info;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Parse a string of JSON objects and turn them into Comments.
     *
     * @param courseJSON A string of JSON objects
     * @return a list of Courses.
     * @throws JSONException
     */
    public static List<Course> parseCourseReviewJSON(String courseJSON) throws JSONException {
        List<Course> courseList = new ArrayList<>();

        if (courseJSON != null) {
            JSONArray array = new JSONArray(courseJSON);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Course course = new Course( obj.getString(COURSEID),
                                            obj.getString(TITLE),
                                            obj.getString(INFO),
                                            obj.getString(EMAIL));
                courseList.add(course);
            }
            Collections.reverse(courseList);
        }
        return courseList;
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.course_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//
//    }



}
