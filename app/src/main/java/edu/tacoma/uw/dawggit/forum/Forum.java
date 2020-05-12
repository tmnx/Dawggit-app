package edu.tacoma.uw.dawggit.forum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Forum implements Serializable {
    private String tid;
    private String title;
    private String content;
    private String date;
    private String email;

    public static final String TID = "thread_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String DATE = "date_created";
    public static final String EMAIL = "email";

    public Forum(String threadId, String aTitle, String aContent, String aDate, String aUser) {
        this.tid = threadId;
        this.title = aTitle;
        this.content = aContent;
        this.date = aDate;
        this.email = aUser;
    }

    public Forum(String aTitle, String aContent, String aUser) {
        this.tid = "temp";
        this.title = aTitle;
        this.content = aContent;
        this.date = "temp";
        this.email = "smith17@uw.edu";
    }

    public String getThreadId() {
        return this.tid;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getDate() {
        return this.date;
    }

    public String getEmail() {
        return this.email;
    }


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