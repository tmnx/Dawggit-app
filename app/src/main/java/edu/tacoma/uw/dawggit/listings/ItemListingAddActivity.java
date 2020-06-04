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
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import edu.tacoma.uw.dawggit.R;

public class ItemListingAddActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private SharedPreferences mSharedPreferences;

    private ImageButton mImageButtonChooseImage;
    private Button mButtonNewListing;
    private ImageButton mFinishButton;

    private EditText mTitle;
    private EditText mDescription;
    private EditText mPrice;

    private Date mDate;
    private Uri mImageUri;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listing_add);

        mSharedPreferences = getSharedPreferences(getString(R.string.USER_EMAIL), Context.MODE_PRIVATE);
        //String userEmail = mSharedPreferences.getString(getString(R.string.USER_EMAIL), null);
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
        String uid = mSharedPreferences.getString(getString(R.string.FIREBASE_UID), null);
        if(uid == null) {
            Log.e("ItemListingAddActivity", "Shared preferences did not pass Firebase UID");
        }
        mStorageReference = FirebaseStorage.getInstance().getReference("listings_uploads");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users/" + uid + "/listings_uploads");

        mImageButtonChooseImage.setOnClickListener(v -> openFileChooser());

        mButtonNewListing.setOnClickListener(v -> {
            if(mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(ItemListingAddActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            }
            else {
                boolean validUpload = uploadFile();
                if(validUpload && mUploadTask.isComplete()) {
                    finish();
                }
            }
        });

        mFinishButton.setOnClickListener(v -> {
            if(mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(ItemListingAddActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null) {
            mImageUri = data.getData();

            //Picasso.get().load(mImageUri).into(mImageButtonChooseImage);
            mImageButtonChooseImage.setImageURI(mImageUri);
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

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private boolean uploadFile() {
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
//                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//                            String date = sdf.format(new Date());
                            assert userEmail != null;
                            assert downloadUrl != null;
                            ItemListing itemListing = new ItemListing(userEmail, title, description, price, mDate, downloadUrl.toString());
                            String uploadId = mDatabaseReference.push().getKey();
                            assert uploadId != null;
                            mDatabaseReference.child(uploadId).setValue(itemListing);

                            DatabaseReference listingsRef = FirebaseDatabase.getInstance().getReference("users/listings");
                            String listingsUploadId = listingsRef.push().getKey();
                            assert listingsUploadId != null;
                            listingsRef.child(listingsUploadId).setValue(itemListing);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ItemListingAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return true;
    }
}
