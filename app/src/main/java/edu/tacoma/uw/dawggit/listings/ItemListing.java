/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.listings;



import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Represents an item a user wants to sell.
 * @version Sprint 2
 * @author Kevin Bui
 */
public class ItemListing implements Serializable {

    /**
     * Email validation pattern.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /** The user's email who created this item listing.*/
    private String mUserEmail;

    /** The title of this item.*/
    private String mTitle;

    /** The description of this item.*/
    private String mTextBody;

    /** The date that this item was created.*/
    private Date mDate;

    /** The price of this item.*/
    private Double mPrice;

    /** The Firebase URL where the image of this item is stored.*/
    private String mImageUrl;

    /** The Firebase unique identifier of this item stored in the Firebase Realtime Database*/
    private String mKey;

    /**
     * Empty Constructor used by firebase realtime database
     */
    public ItemListing() {

    }

    /**
     * Constructor for ItemListing
     * @param userEmail String
     * @param title String
     * @param textBody String
     * @param price Double
     * @param date Date
     * @param url String
     */
    public ItemListing(String userEmail, String title, String textBody, Double price, Date date, String url) {
        if(!isValidEmail(userEmail.trim())) {
            throw new IllegalArgumentException("Invalid email");
        }
        if(title.isEmpty() || title.length() > 50) {
            throw new IllegalArgumentException("Invalid title");
        }
        if(textBody.isEmpty() || textBody.length() > 255) {
            throw new IllegalArgumentException("Invalid text body");
        }
        if(price < 0) { //\d{1,5}\.\d\d
            throw new IllegalArgumentException("Invalid price");
        }
        if(date == null) {
            throw new NullPointerException("Date is null");
        }
        if(url.isEmpty()) {
            throw new IllegalArgumentException("No Image Url");
        }
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


    /**
     * Unique key representing an entry in the Firebase Realtime Database.
     * @return String key
     */
    @Exclude
    public String getKey() {return this.mKey;}

    /**
     * Sets the unique key of this item to another key in regards to the Firebase Realtime Database.
     * @param key Sets this items unique firebase key to key
     */
    @Exclude
    public void setKey(String key) {mKey = key;}

    /**
     * Validates if the given input is a valid email address.
     *
     * @param email The email to validate.
     * @return {@code true} if the input is a valid email. {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

}
