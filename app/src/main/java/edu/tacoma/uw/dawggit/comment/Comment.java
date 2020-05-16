package edu.tacoma.uw.dawggit.comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a comment object to hold all required information to create
 * an entry to the database in SQL.
 *
 * @author Minh Nguyen
 */
public class Comment {

    /**
     * User email.
     */
    private String email;

    /**
     * Thread identification number.
     */
    private String thread_id;

    /**
     * The date the comment was created.
     */
    private String date;

    /**
     * The content of the comment.
     */
    private String content;

    /**
     * Email in sql.
     */
    public static final String EMAIL = "email";

    /**
     * Thread id in sql.
     */
    public static final String THREAD_ID = "thread_id";

    /**
     * Date in sql.
     */
    public static final String DATE = "date_posted";

    /**
     * Content in sql.
     */
    public static final String CONTENT = "content";

    /**
     * Create a comment.
     *
     * @param theEmail    user email
     * @param theThreadid thread id
     * @param theDate     date the comment is posted
     * @param theContent  comment content
     */
    public Comment(String theEmail, String theThreadid,
                   String theDate, String theContent) {
        email = theEmail;
        thread_id = theThreadid;
        date = theDate;
        content = theContent;
    }

    /**
     * Create a comment.
     *
     * @param theEmail   user email
     * @param theContent comment content
     */
    public Comment(String theEmail, String theThread_id, String theContent) {
        email = theEmail;
        thread_id = theThread_id;
        date = "temp";
        content = theContent;
    }

    // GETTERS
    public String getEmail() {
        return email;
    }

    public String getThread_id() {
        return thread_id;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    /**
     * Parse a string of JSON objects and turn them into Comments.
     *
     * @param commentJSON string on JSON objects
     * @return a list of comments
     * @throws JSONException
     */
    public static List<Comment> parseCommentJSON(String commentJSON) throws JSONException {
        List<Comment> commentList = new ArrayList<>();

        if (commentJSON != null) {
            JSONArray array = new JSONArray(commentJSON);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Comment comment = new Comment(  obj.getString(EMAIL),
                                                obj.getString(THREAD_ID),
                                                obj.getString(DATE),
                                                obj.getString(CONTENT));
                commentList.add(comment);
            }
        }
        return commentList;
    }

}
