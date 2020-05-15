package edu.tacoma.uw.dawggit.course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Course {

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

}
