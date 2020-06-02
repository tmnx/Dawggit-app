package edu.tacoma.uw.dawggit.course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.review.ReviewAddActivity;
import edu.tacoma.uw.dawggit.review.ReviewsContent;

public class CourseDisplayActivity extends AppCompatActivity {

    /**
     * Item ID used for this activity.
     */
    public static final String ARG_ITEM_ID = "course_item_id";

    /**
     * Forum being displayed by this activity.
     */
    private Course mCourse;

    /**
     * Set up the buttons listeners and functions.
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_display);

        if (getIntent().getSerializableExtra(ARG_ITEM_ID) != null) {
            // to load content from a content provider.
            mCourse = (Course) getIntent().getSerializableExtra(ARG_ITEM_ID);
        }
        if (mCourse != null) {
            ((TextView) findViewById((R.id.textViewCourseID))).setText(mCourse.getCourse_code());
            ((TextView) findViewById((R.id.textViewCourseTitle))).setText(mCourse.getTitle());
            ((TextView) findViewById((R.id.textViewCourseContent))).setText(mCourse.getCourse_info());
        }

        ImageView closeButton = findViewById(R.id.closeCourseView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button addReviewButton = findViewById(R.id.buttonAddReview);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                                        ReviewAddActivity.class);
                i.putExtra("course_id", mCourse.getCourse_code());
                startActivity(i);
            }
        });

        Button viewRepButton = (Button) findViewById(R.id.buttonViewReviews);
        viewRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CourseDisplayActivity.this,
                                        ReviewsContent.class);
                i.putExtra("course_id", mCourse.getCourse_code());
                startActivity(i);
            }
        });

    }
}
