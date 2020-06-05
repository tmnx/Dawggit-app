package edu.tacoma.uw.dawggit.listings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.tacoma.uw.dawggit.R;

/**
 * This activity is created when a user clicks on an item listing in ListingsFragment or HomeFragment.
 * Shows the item listing details on a single activity.
 * @version Sprint 2
 * @author Kevin Bui
 */
public class ItemListingDetail extends AppCompatActivity {

    /** The item to be displayed.*/
    private ItemListing mItemListing;

    /**
     * Creates the activity.
     * @param savedInstanceState null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listing_detail);

        //ARG_ITEM should never be null
        if (getIntent().getSerializableExtra("ARG_ITEM_ID") != null) {
            mItemListing = (ItemListing) getIntent().getSerializableExtra("ARG_ITEM_ID");
        }
        if (mItemListing != null) {
            TextView titleTextView = findViewById(R.id.listing_detail_title);
            TextView priceTextView = findViewById(R.id.listing_detail_price);
            TextView textBodyTextView = findViewById(R.id.listing_detail_body);
            TextView dateTextView = findViewById(R.id.listing_detail_date);
            ImageView imageView = findViewById(R.id.listing_detail_imageView);

            titleTextView.setText((mItemListing.getTitle()));
            DecimalFormat decForm = new DecimalFormat("0.00");
            String priceFormat = "$" + decForm.format(mItemListing.getPrice());
            priceTextView.setText(priceFormat);
            textBodyTextView.setText(mItemListing.getTextBody());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
            String date = sdf.format(mItemListing.getDate());
            dateTextView.setText(date);
            Picasso.get().load(mItemListing.getUrl()).into(imageView);
        }
        ImageButton finishButton = findViewById(R.id.listing_detail_finish);
        finishButton.setOnClickListener(v -> finish()); // Destroys the activity.

        ImageButton emailButton = findViewById(R.id.listing_detail_email_button);
        emailButton.setOnClickListener(v -> { //Lets users email the owner of the item listing.
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ mItemListing.getEmail()});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Buying [" + mItemListing.getTitle() + "]");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }
}
