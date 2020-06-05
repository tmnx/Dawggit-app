package edu.tacoma.uw.dawggit.listings;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.database.Exclude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

public class ItemListing implements Serializable {
    private String mUserEmail;
    private String mTitle;
    private String mTextBody;
    private Date mDate;
    private Double mPrice;
    private String mImageUrl;
    private String mKey;

    /**
     * Empty Constructor used by firebase realtime database
     */
    public ItemListing() {

    }

    /**
     * Constructor
     * @param userEmail String
     * @param title String
     * @param textBody String
     * @param price Double
     * @param date Date
     * @param url String
     */
    ItemListing(String userEmail, String title, String textBody, Double price, Date date, String url) {
        this.mUserEmail = userEmail.trim();
        this.mTitle = title.trim();
        this.mTextBody = textBody.trim();
        this.mPrice = price;
        this.mDate = date;
        this.mImageUrl = url;
    }

    public String getEmail() {
        return this.mUserEmail;
    }

    public void setEmail(String email) { this.mUserEmail = email; }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) { this.mTitle = title; }

    public String getTextBody() {
        return this.mTextBody;
    }

    public void setTextBody(String textBody) { this.mTextBody = textBody; }

    public Double getPrice() {return this.mPrice; }

    public void setPrice(Double price) { this.mPrice = price; }

    public Date getDate() { return this.mDate; }

    public void setDate(Date date) { this.mDate = date; }

    public String getUrl() { return this.mImageUrl; }

    public void setUrl(String url) { this.mImageUrl = url; }


    @Exclude
    public String getKey() {return this.mKey;}

    @Exclude
    public void setKey(String key) {mKey = key;}

}
