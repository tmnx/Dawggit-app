package edu.tacoma.uw.dawggit.review;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

public class ReviewDB {

    /**
     * Database version
     */
    public static final int DB_VERSION = 1;

    /**
     * Database name
     */
    public static final String DB_NAME = "Reviews.db";

    /**
     * Helper to create and drop table.
     */
    private ReviewDBHelper mReviewDBHelper;

    /**
     * Local database.
     */
    private SQLiteDatabase mSqLiteDatabase;

    /**
     * Creates a Review data base
     *
     * @param context context
     */
    public ReviewDB(Context context) {
        mReviewDBHelper = new ReviewDBHelper(context, DB_NAME, null, DB_VERSION);
        mSqLiteDatabase = mReviewDBHelper.getWritableDatabase();
    }

    /**
     * Insert a review into local database.
     *
     * @param email       user email
     * @param course_code course to be reviewed
     * @param content     review content
     * @return whether review is added successfully
     */
    public boolean insertReview(String email, String course_code, String date, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("course_code", course_code);
        contentValues.put("date_posted", date);
        contentValues.put("content", content);

        long rowId = mSqLiteDatabase.insert("Reviews", null, contentValues);
        return rowId != -1;
    }

    /**
     * Delete reviews for a certain course.
     */
    public void deleteReviews(String course_code) {
        mSqLiteDatabase.delete("Reviews", "course_code=?",
                new String[]{course_code});
    }

    /**
     * Delete reviews for a certain course.
     */
    public void deleteAllReviews() {
        mSqLiteDatabase.delete("Reviews", null, null);
    }

    /**
     * Get a list of all the reviews of a specific course.
     *
     * @param theCourse_code the course to get reviews from
     * @return a list of reviews of the course
     */
    public List<Review> getReviews(String theCourse_code) {
        String[] columns = {
                "email", "course_code", "date_posted", "content"
        };

        Cursor c = mSqLiteDatabase.query("Reviews",
                                        columns,
                                "course_code=?",
                                        new String[] {theCourse_code},
                                null, null, null);
        c.moveToFirst();
        List<Review> reviewList = new ArrayList<>();

        for (int i = 0; i < c.getCount(); i++) {
            String email = c.getString(0);
            String course_code = c.getString(1);
            String date_posted = c.getString(2);
            String content = c.getString(3);

            Review review = new Review(email, course_code, date_posted, content);
            reviewList.add(review);
            c.moveToNext();
        }

        return reviewList;
    }

    /**
     * Helper class to manage the Review db.
     */
    private class ReviewDBHelper extends SQLiteOpenHelper {

        /**
         * Create a review table string in sql.
         */
        private final String CREATE_REVIEW_SQL;

        /**
         * Drop a review table in sql.
         */
        private final String DROP_REVIEW_SQL;

        /**
         * Creates a db helper for Review.
         *
         * @param context the context
         * @param name    name
         * @param factory cursor factory
         * @param version db version
         */
        public ReviewDBHelper(Context context,
                              String name,
                              SQLiteDatabase.CursorFactory factory,
                              int version) {
            super(context, name, factory, version);
            CREATE_REVIEW_SQL = context.getString(R.string.CREATE_REVIEW_SQL);
            DROP_REVIEW_SQL = context.getString(R.string.DROP_REVIEW_SQL);
        }

        /**
         * Create a local database.
         * @param sqLiteDatabase local database.
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_REVIEW_SQL);
        }

        /**
         * Update the local database.
         * @param db local database.
         * @param oldVersion old version
         * @param newVersion new version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_REVIEW_SQL);
            onCreate(db);
        }
    }
}
