package edu.tacoma.uw.dawggit.course;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

public class Course  {

    private String course_code;
    private String title;
    private String course_info;
    private String email;

    public static final String COURSEID = "course_code";
    public static final String TITLE = "title";
    public static final String INFO = "course_info";
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

    public static List<Course> parseCourseJSON(String courseJSON) throws JSONException {
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
