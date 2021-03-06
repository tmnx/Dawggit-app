/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.comment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

/**
 * Creates a local Comment Database. User can load the app from local database
 * when internet connection is not available.
 *
 * @author Minh Nguyen
 */
public class CommentDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Comments.db";

    /**
     * Helper to create and drop local database.
     */
    private CommentDBHelper mCommentDBHelper;

    /**
     * Local database.
     */
    private SQLiteDatabase mSqLiteDatabase;

    /**
     * Create a local Comment database.
     *
     * @param context the context
     */
    public CommentDB(Context context) {
        mCommentDBHelper = new CommentDBHelper(context, DB_NAME, null, DB_VERSION);
        mSqLiteDatabase = mCommentDBHelper.getWritableDatabase();
    }

    /**
     * Insert a comment into the local database.
     *
     * @param email user mail
     * @param thread_id the thread the comment is on
     * @param date date posted
     * @param content comment content
     * @return
     */
    public boolean insertComment(String email, String thread_id, String date, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("thread_id", thread_id);
        contentValues.put("date_posted", date);
        contentValues.put("content", content);

        long rowID = mSqLiteDatabase.insert("Comments", null, contentValues);
        return rowID != -1;
    }

//    public void deleteComments(String thread_id) {
//        mSqLiteDatabase.delete("Comments", "thread_id=?",
//                                new String[] {thread_id});
//    }

    /**
     * Get a list of comments of a specified thread.
     *
     * @param thread_id the thread identification
     * @return a list of comments
     */
    public List<Comment> getComments(String thread_id) {
        String[] columns = {
                "email", "thread_id", "date_posted", "content"
        };

        Cursor c = mSqLiteDatabase.query("Comments",
                columns,
                "thread_id=?",
                new String[] {thread_id},
                null, null, null);
        c.moveToFirst();
        List<Comment> reviewList = new ArrayList<>();

        for (int i = 0; i < c.getCount(); i++) {
            String email = c.getString(0);
            String threadid = c.getString(1);
            String date_posted = c.getString(2);
            String content = c.getString(3);

            Comment comment = new Comment(email, threadid, date_posted, content);
            reviewList.add(comment);
            c.moveToNext();
        }

        return reviewList;
    }

    /**
     * Clear the comment table.
     */
    public void deleteAllComments() {
        mSqLiteDatabase.delete("Comments", null, null);
    }

    /**
     * Helper class to manage local Comment db.
     */
    class CommentDBHelper extends SQLiteOpenHelper {

        /**
         * Create table string
         */
        private final String CREATE_COMMENT_SQL;

        /**
         * Drop table string
         */
        private final String DROP_COMMENT_SQL;

        /**
         * Create a local helper to create and drop the Comment database.
         *
         * @param context context
         * @param name local database name
         * @param factory CursorFactory
         * @param version database version
         */
        public CommentDBHelper(Context context,
                               String name,
                               SQLiteDatabase.CursorFactory factory,
                               int version) {
            super(context, name, factory, version);
            CREATE_COMMENT_SQL = context.getString(R.string.CREATE_COMMENTS_SQL);
            DROP_COMMENT_SQL = context.getString(R.string.DROP_COMMENTS_SQL);
        }

        /**
         * Create a local database.
         *
         * @param db the local database
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_COMMENT_SQL);
        }

        /**
         * Drop and create a new database.
         *
         * @param db local database
         * @param oldVersion old version
         * @param newVersion new version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_COMMENT_SQL);
            onCreate(db);
        }
    }
}
