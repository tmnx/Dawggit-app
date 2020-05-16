package edu.tacoma.uw.dawggit.forum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Forum Class
 * Stores information about a forum posting
 * @version Sprint1
 * @author Sean Smith
 */
public class Forum implements Serializable {
    /**
     * Thread id of thread
     */
    private String tid;
    /**
     * Title of post
     */
    private String title;
    /**
     * Contents of forum post
     */
    private String content;
    /**
     * Date thread was posted
     */
    private String date;
    /**
     * Email of user that posted
     */
    private String email;

    /**
     * Static string for thread id
     */
    public static final String TID = "thread_id";
    /**
     * Static string for finding title of post
     */
    public static final String TITLE = "title";
    /**
     * Static string for finding content of post
     */
    public static final String CONTENT = "content";
    /**
     * Static string for date course was posted
     */
    public static final String DATE = "date_created";
    /**
     * Static string for finding email
     */
    public static final String EMAIL = "email";

    /**
     * Constructor for building a Forum object
     * @param threadId ID of the thread
     * @param aTitle title of the thread
     * @param aContent Content of thread posting
     * @param aDate Date user posted thread
     * @param aUser Email of user that posted thread
     */
    public Forum(String threadId, String aTitle, String aContent, String aDate, String aUser) {
        this.tid = threadId;
        this.title = aTitle;
        this.content = aContent;
        this.date = aDate;
        this.email = aUser;
    }

    /**
     * Constructor for when we do not know the thread id, before the post request
     * @param aTitle Title of thread that user posted
     * @param aContent Content of thread that user posted
     * @param aUser Email of user that posted thread
     */
    public Forum(String aTitle, String aContent, String aUser) {
        this.tid = "temp";
        this.title = aTitle;
        this.content = aContent;
        this.date = "temp";
        this.email = "smith17@uw.edu";
    }

    /**
     * Returns the thread ID
     * @return ID of thread
     */
    public String getThreadId() {
        return this.tid;
    }

    /**
     * Returns title of post
     * @return Title of the post
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the body of the post
     * @return Body of text contained in the post
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Date the post was posted
     * @return Date post was created
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Retunrns email of person that posted
     * @return Email of person that posted
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Parses JSON string list of courses into forums
     * @param courseJson JSON that contains the forum post information
     * @return A list of forum posts based on the inputted json
     * @throws JSONException If there is an error in parsing JSON it is thrown
     */
    public static List<Forum> parseCourseJson(String courseJson) throws JSONException {
        List<Forum> forumList = new ArrayList<>();
        if (courseJson != null) {
            JSONArray arr = new JSONArray(courseJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Forum forum = new Forum(obj.getString(Forum.TID), obj.getString(Forum.TITLE), obj.getString(Forum.CONTENT), obj.getString(Forum.DATE), obj.getString(Forum.EMAIL));
                forumList.add(forum);
            }
        }
        Collections.reverse(forumList);
        return forumList;
    }
}