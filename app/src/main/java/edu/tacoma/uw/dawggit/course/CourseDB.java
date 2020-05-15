package edu.tacoma.uw.dawggit.course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

public class CourseDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Course.db";

    private CourseDBHelper mCourseDBHelper;
    private SQLiteDatabase mSqLiteDatabase;

    /**
     * Create a local Course database.
     *
     * @param context context
     */
    public CourseDB(Context context) {
        mCourseDBHelper = new CourseDBHelper(context, DB_NAME, null, DB_VERSION);
        mSqLiteDatabase = mCourseDBHelper.getWritableDatabase();
    }

    /**
     * Insert a course into local db.
     *
     * @param course_code course code
     * @param title course title
     * @param info course info
     * @param email user email
     * @return whether course was added in successfully
     */
    public boolean insertCourse(String course_code, String title, String info, String email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("course_code", course_code);
        contentValues.put("title", title);
        contentValues.put("course_info", info);
        contentValues.put("email", email);

        long rowID = mSqLiteDatabase.insert("Course", null, contentValues);
        return rowID != -1;
    }

    /**
     * Delete all courses from local db.
     */
    public void deleteCourses() {
        mSqLiteDatabase.delete("Course", null, null);
    }

    public List<Course> getCourses() {
        String[] columns = {"course_code", "title", "course_info", "email"};

        Cursor c = mSqLiteDatabase.query(
                "Course",
                columns,
                null,
                null,
                null,
                null,
                null
        );
        c.moveToFirst();
        List<Course> courseList = new ArrayList<>();

        for(int i = 0; i < c.getCount(); i++) {
            String courseID = c.getString(0);
            String title = c.getString(1);
            String course_info = c.getString(2);
            String email = c.getString(3);

            Course course = new Course(courseID, title, course_info, email);
            courseList.add(course);
            c.moveToNext();
        }

        return courseList;
    }

    /**
     * Helper class to manage local Course db.
     */
    class CourseDBHelper extends SQLiteOpenHelper {

        private final String CREATE_COURSE_SQL;
        private final String DROP_COURSE_SQL;

        public CourseDBHelper(Context context,
                               String name,
                               SQLiteDatabase.CursorFactory factory,
                               int version) {
            super(context, name, factory, version);
            CREATE_COURSE_SQL = context.getString(R.string.CREATE_COURSE_SQL);
            DROP_COURSE_SQL = context.getString(R.string.DROP_COURSE_SQL);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_COURSE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_COURSE_SQL);
            onCreate(db);
        }
    }


}
