package edu.tacoma.uw.dawggit.forum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

/**
 * Database class for SQL lite for Forum Posts
 * @author Sean Smith
 * @version Sprint 1
 */
public class ForumDb {
    /**
     * Version of database
     */
    private static final int DB_VERSION = 1;
    /**
     * Name of database for SQL lite
     */
    private static final String DB_NAME = "Forum.db";
    /**
     * Used to help manage the database
     */
    private ForumDBHelper mForumDBHelper;
    /**
     * SQLLite database object used for adding and retrieving
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Creates a new Forum Database Object
     * @param context Context object was created in.
     */
    public ForumDb(Context context) {
        mForumDBHelper = new ForumDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mForumDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the forum into the local forum table. Returns true if successful, false otherwise.
     * @return true or false
     */
    public boolean insertForum(String tid, String title, String content, String date_created, String email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("thread_id", tid);
        contentValues.put("title", title);
        contentValues.put("content", content);
        contentValues.put("date_created", date_created);
        contentValues.put("email", email);


        long rowId = mSQLiteDatabase.insert("Thread", null, contentValues);
        return rowId != -1;
    }

    /**
     * Delete all the data from the Forums
     */
    public void deleteCourses() {
        mSQLiteDatabase.delete("Thread", null, null);
    }

    /**
     * Returns the list of courses from the local Forum table.
     * @return list
     */
    public List<Forum> getThreads() {

        String[] columns = {
                "thread_id", "title", "content", "date_created", "email"
        };

        Cursor c = mSQLiteDatabase.query(
                "Thread",  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Forum> list = new ArrayList<>();
        for (int i=0; i<c.getCount(); i++) {
            String tid = c.getString(0);
            String title = c.getString(1);
            String content = c.getString(2);
            String date_created = c.getString(3);
            String email = c.getString(4);
            Forum forum = new Forum(tid, title, content, date_created, email);
            list.add(forum);
            c.moveToNext();
        }

        return list;
    }


    /**
     * Class helps in creation/deletion of forum database
     */
    class ForumDBHelper extends SQLiteOpenHelper {
        /**
         * String for creating the database.
         */
        private final String CREATE_FORUM_SQL;
    /**
     * String for deleting the database.
     */
        private final String DROP_FORUM_SQL;

        /**
         * Constructor for database helper.
         * @param context Context for which the object was created.
         * @param name Name of the object.
         * @param factory Factory of the object.
         * @param version Version of the database.
         */
        public ForumDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_FORUM_SQL = context.getString(R.string.CREATE_THREAD_SQL);
            DROP_FORUM_SQL = context.getString(R.string.DROP_THREAD_SQL);

        }

        /**
         * When the database is created this is called
         * @param sqLiteDatabase SQLiteDatabase that is being created.
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_FORUM_SQL);
        }

        /**
         * Drops tables from old database on new database creation
         * @param sqLiteDatabase Database which tables are being dropped
         * @param i A string that is not being used but is required by super()
         * @param i1 A string that is not being used but is required by super()
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_FORUM_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}