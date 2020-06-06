/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Store information of a Review.
 *
 * @author Minh Nguyen
 */
public class Review implements Serializable {

    /**
     * User that created the review.
     */
    private String email;

    /**
     * The course code to review on.
     */
    private String course_code;

    /**
     * Date the review was created.
     */
    private String date;

    /**
     * The content of the review.
     */
    private String content;

    /**
     * The email string for sql.
     */
    public static final String EMAIL = "email";

    /**
     * The course code string for sql.
     */
    public static final String COURSE_CODE = "course_code";

    /**
     * The date string for sql
     */
    public static final String DATE = "date_posted";

    /**
     * The content string for sql.
     */
    public static final String CONTENT = "content";

    /**
     * Creates a Review object.
     * @param theEmail user email
     * @param theCourseCode course to be reviewed on
     * @param theDate date of when the review was posted
     * @param theContent content of the review
     */
    public Review(String theEmail, String theCourseCode, String theDate, String theContent) {
        email = theEmail;
        course_code = theCourseCode;
        date = theDate;
        content = theContent;
    }

    /**
     * Creates a Review object.
     * @param theEmail user email
     * @param theCourseCode course to be reviewed on
     * @param theContent content of the review
     */
    public Review(String theEmail, String theCourseCode, String theContent) {
        email = theEmail;
        course_code = theCourseCode;
        content = theContent;
    }

    /**
     * Get user email.
     * @return user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get course code.
     * @return course code
     */
    public String getCourse_code() {
        return course_code;
    }

    /**
     * Get posted date.
     * @return posted date
     */
    public String getDate() {
        return date;
    }

    /**
     * Get content of review
     * @return review content
     */
    public String getContent() {
        return content;
    }

    /**
     * Parse through a JSON string and create JSON objects (reviews).
     *
     * @param reviewJSON passed in JSON string of reviews
     * @return a list of Reviews
     * @throws JSONException
     */
    public static List<Review> parseReviewJSON(String reviewJSON) throws JSONException {
        List<Review> reviewList = new ArrayList<>();

        if (reviewJSON != null) {
            JSONArray array = new JSONArray(reviewJSON);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Review review = new Review( obj.getString(EMAIL),
                                            obj.getString(COURSE_CODE),
                                            obj.getString(DATE),
                                            obj.getString(CONTENT));
                reviewList.add(review);
            }
        }
        Collections.reverse(reviewList);
        return reviewList;
    }
}
