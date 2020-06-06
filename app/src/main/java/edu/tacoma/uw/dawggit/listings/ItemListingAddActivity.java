/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.listings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.tacoma.uw.dawggit.R;

/**
 * Allows users to add item listings to the Firebase Realtime Database,
 * and stores item images in Firebase Storage.
 * @version Sprint 2
 * @author Kevin Bui
 */
public class ItemListingAddActivity extends AppCompatActivity {

    /** Used for choosing images from the device.*/
    private static final int PICK_IMAGE_REQUEST = 1;

    /** Used to access the current user's email.*/
    private SharedPreferences mSharedPreferences;

    /** Button to choose images.*/
    private ImageButton mImageButtonChooseImage;

    /** Button to submit a new item listing.*/
    private Button mButtonNewListing;

    /** Button to exit out of the activity.*/
    private ImageButton mFinishButton;

    /** Editable title.*/
    private EditText mTitle;

    /** Editable description of the item listing.*/
    private EditText mDescription;

    /** Editable price of the item listing.*/
    private EditText mPrice;

    /** Date that the item listing was created.*/
    private Date mDate;

    /** File path of the image.*/
    private Uri mImageUri;

    /** Reference to the Firebase storage to store images.*/
    private StorageReference mStorageReference;

    /** Reference to the Firebase Realtime Database to store the item listing.*/
    private DatabaseReference mDatabaseReference;

    /** Task to upload the newly created item listing to Firebase*/
    private StorageTask mUploadTask;

    /**
     *
     * @param savedInstanceState null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listing_add);

        mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        mImageButtonChooseImage = findViewById(R.id.imageButton);
        mButtonNewListing = findViewById(R.id.button_listing_new_post);
        mFinishButton = findViewById(R.id.listing_add_finish_button);

        mTitle = findViewById(R.id.et_listing_title);
        mDescription = findViewById(R.id.et_listing_body);
        mPrice = findViewById(R.id.et_listing_price);
        mDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        String date = sdf.format(mDate);
        TextView dateTextView = findViewById(R.id.tv_listing_date);
        dateTextView.setText(date);

        mSharedPreferences = getSharedPreferences(getString(R.string.FIREBASE_UID), MODE_PRIVATE);

        mStorageReference = FirebaseStorage.getInstance().getReference("listings_images");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users/listings");

        mImageButtonChooseImage.setOnClickListener(v -> openFileChooser());

        mButtonNewListing.setOnClickListener(v -> { //Adds a new item to the firebase database.
            if(mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(ItemListingAddActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            }
            else {
                boolean validUpload = uploadItemListing();
                if(validUpload) {
                    finish();
                }
            }
        });

        mFinishButton.setOnClickListener(v -> { //Exits the activity.
            if(mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(ItemListingAddActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });
    }

    /**
     *
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Gets the file path for the selected image.
     * @param requestCode request code
     * @param resultCode result code
     * @param data Returns an Image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageButtonChooseImage);
            //mImageButtonChooseImage.setImageURI(mImageUri);
        }
        else if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Result Code: " + resultCode, Toast.LENGTH_SHORT).show();
        }
        else if (data == null) {
            Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the file extension of images.
     * @param uri image uri
     * @return string of file extension
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    /**
     * Uploads the newly created item listing to Firebase.
     * @return boolean, if the upload was successful or not.
     */
    private boolean uploadItemListing() {
        if(TextUtils.isEmpty(mTitle.getText())) {
            Toast.makeText(ItemListingAddActivity.this, "Title is Required", Toast.LENGTH_SHORT).show();
            mTitle.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(mDescription.getText())) {
            Toast.makeText(ItemListingAddActivity.this, "Description is Required", Toast.LENGTH_SHORT).show();
            mDescription.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(mPrice.getText())) {
            Toast.makeText(ItemListingAddActivity.this, "Price is required", Toast.LENGTH_SHORT).show();
            mPrice.requestFocus();
            return false;
        }
        else if (mImageUri == null) {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  {
            StorageReference fileReference = mStorageReference
                    .child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ItemListingAddActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), MODE_PRIVATE);
                            String userEmail = mSharedPreferences.getString(getString(R.string.USER_EMAIL), null);
                            String title = mTitle.getText().toString();
                            String description = mDescription.getText().toString();
                            Double price = Double.parseDouble(mPrice.getText().toString());
                            assert userEmail != null;
                            assert downloadUrl != null;
                            ItemListing itemListing = new ItemListing(userEmail, title, description, price, mDate, downloadUrl.toString());
                            String uploadId = mDatabaseReference.push().getKey();
                            assert uploadId != null;
                            mDatabaseReference.child(uploadId).setValue(itemListing);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ItemListingAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
    }
}
