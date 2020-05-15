package edu.tacoma.uw.dawggit.comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private String email;
    private String thread_id;
    private String date;
    private String content;

    public static final String EMAIL = "email";
    public static final String THREAD_ID = "thread_id";
    public static final String DATE = "date_posted";
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
