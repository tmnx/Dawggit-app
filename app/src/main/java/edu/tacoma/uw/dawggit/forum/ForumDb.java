package edu.tacoma.uw.dawggit.forum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;


public class ForumDb {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Forum.db";

    private CourseDBHelper mForumDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public ForumDb(Context context) {
        mForumDBHelper = new CourseDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mForumDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
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
     * Delete all the data from the Courses
     */
    public void deleteCourses() {
        mSQLiteDatabase.delete("Thread", null, null);
    }

    /**
     * Returns the list of courses from the local Course table.
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





    class CourseDBHelper extends SQLiteOpenHelper {

        private final String CREATE_COURSE_SQL;

        private final String DROP_COURSE_SQL;

        public CourseDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_COURSE_SQL = context.getString(R.string.CREATE_THREAD_SQL);
            DROP_COURSE_SQL = context.getString(R.string.DROP_THREAD_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_COURSE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_COURSE_SQL);
            onCreate(sqLiteDatabase);
        }
    }

}