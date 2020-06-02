package edu.tacoma.uw.dawggit.forum;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.comment.CommentAddActivity;
import edu.tacoma.uw.dawggit.comment.CommentsContent;

/**
 * Activity that displays a forum post
 * @author Sean Smith
 * @version Sprint1
 */
public class ForumDisplayActivity extends AppCompatActivity {
    /**
     * Item ID used for this activity.
     */
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * Thread ID for this forum
     */
    public String mThreadID;
    /**
     * Forum being displayed by this activity.
     */
    private Forum mForum;

    /**
     * Sets up this activity and initalizes all of the text boxes
     * @param savedInstanceState State required by parent class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_display);

        if (getIntent().getSerializableExtra(ARG_ITEM_ID) != null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mForum = (Forum) getIntent().getSerializableExtra(ARG_ITEM_ID);
        }
        if (mForum != null) {
            ((TextView) findViewById((R.id.page_title))).setText(mForum.getTitle());
            ((TextView) findViewById((R.id.page_date))).setText(mForum.getDate());
            ((TextView) findViewById((R.id.page_email))).setText(mForum.getEmail());
            ((TextView) findViewById((R.id.post_content))).setText(mForum.getContent());
        }

        ImageButton finishButton = findViewById(R.id.end);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button replyButton = (Button) findViewById(R.id.replyButton);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForumDisplayActivity.this,
                                        CommentAddActivity.class);
                i.putExtra("thread_id", mForum.getThreadId());
                startActivity(i);
            }
        }) ;

        Button viewRepButton = (Button) findViewById(R.id.viewRepliesButton);
        viewRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForumDisplayActivity.this,
                                        CommentsContent.class);
                i.putExtra("thread_id", mForum.getThreadId());
                startActivity(i);
            }
        });

    }
}
