package edu.tacoma.uw.dawggit.forum;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.tacoma.uw.dawggit.R;

public class ForumDisplayActivity extends AppCompatActivity {
    public static final String ARG_ITEM_ID = "item_id";
    private Forum mForum;
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

    }
}
