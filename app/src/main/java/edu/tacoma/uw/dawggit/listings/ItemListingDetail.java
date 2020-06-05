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

public class ItemListingDetail extends AppCompatActivity {

    private ItemListing mItemListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listing_detail);

        if (getIntent().getSerializableExtra("ARG_ITEM_ID") != null) {
            mItemListing = (ItemListing) getIntent().getSerializableExtra("ARG_ITEM_ID");
        }
        if (mItemListing != null) {
            TextView titleTextView = findViewById(R.id.listing_detail_title);
            TextView priceTextView = findViewById(R.id.listing_detail_price);
            TextView textBodyTextView = findViewById(R.id.listing_detail_body);
            TextView dateTextView = findViewById(R.id.listing_detail_date);
            ImageView imageView = findViewById(R.id.listing_detail_imageView);
            TextView emailTextView = findViewById(R.id.listing_detail_email);

            titleTextView.setText((mItemListing.getTitle()));
            DecimalFormat decForm = new DecimalFormat("0.00");
            String priceFormat = "$" + decForm.format(mItemListing.getPrice());
            priceTextView.setText(priceFormat);
            textBodyTextView.setText(mItemListing.getTextBody());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
            String date = sdf.format(mItemListing.getDate());
            dateTextView.setText(date);
            emailTextView.setText("Contact: " + mItemListing.getEmail());
            Picasso.get().load(mItemListing.getUrl()).into(imageView);

        }
        ImageButton finishButton = findViewById(R.id.listing_detail_finish);
        finishButton.setOnClickListener(v -> finish());

        ImageButton emailButton = findViewById(R.id.listing_detail_email_button);
        emailButton.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ mItemListing.getEmail()});
            email.putExtra(Intent.EXTRA_SUBJECT, "Buying [" + mItemListing.getTitle() + "]");
            email.setDataAndType(Uri.parse("mailto:"), "message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        });
    }
}
