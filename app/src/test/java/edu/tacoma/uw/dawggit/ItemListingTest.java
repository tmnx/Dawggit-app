/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.tacoma.uw.dawggit.listings.ItemListing;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test class for ItemListing
 * @version Sprint 2
 * @author Kevin Bui
 */
public class ItemListingTest {

    /** Test ItemListing*/
    private  ItemListing testItemListing;

    /**
     * Initializes testItemListing.
     */
    @Before
    public void setTestItemListing() {
        testItemListing = new ItemListing("buik3v@uw.edu", "good title", "good description", 11.00, new Date(), "image.jpg");
    }

    /**
     * Tests the empty ItemListing constructor used for Firebase.
     */
    @Test
    public void testItemListingEmptyConstructor() {
        assertNotNull(new ItemListing());
    }

    /**
     * Tests an invalid email for an item listing.
     */
    @Test
    public void testItemListingConstructorBadEmail() {
        try {
            new ItemListing("buik3v.edu", "good title", "good description", 11.00, new Date(), "image.jpg");
            fail("item created with invalid email");
        } catch(IllegalArgumentException e) {

        }
    }

    /**
     * Tests for an email that is too long for an item listing.
     */
    @Test
    public void testItemListingConstructorOverflowEmail() {
        try {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 500; i++) {
                sb.append("a");
            }
            sb.append("@uw.edu");
            new ItemListing(sb.toString(), "good description", "good description", 11.00, new Date(), "image.jpg");
            fail("item email contains too many characters");
        } catch(IllegalArgumentException e) {

        }
    }

    /**
     * Tests if an ItemListing contains an empty title.
     */
    @Test
    public void testItemListingEmptyTitle() {
        try {
            new ItemListing("buik3v@uw.edu", "", "title", 11.00, new Date(), "image.jpg");
            fail("Item Listing title is empty");
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Tests if an ItemListing has too long of a title.
     */
    @Test
    public void testItemListingOverflowTitle() {
        try {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 300; i++) {
                sb.append("a");
            }
            new ItemListing("buik3v@uw.edu", sb.toString(), "good description", 11.00, new Date(), "image.jpg");
            fail("item title contains too many characters");
        } catch(IllegalArgumentException e) {

        }
    }

    /**
     * Tests if an ItemListing has an empty description.
     */
    @Test
    public void testItemListingEmptyTextBody() {
        try {
            new ItemListing("buik3v@uw.edu", "sdfsd", "", 11.00, new Date(), "image.jpg");
            fail("Item Listing textBody is empty");
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Tests if an ItemListing's description is too long.
     */
    @Test
    public void testItemListingOverflowTextBody() {
        try {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 300; i++) {
                sb.append("a");
            }
            new ItemListing("buik3v@uw.edu", "good title", sb.toString(), 11.00, new Date(), "image.jpg");
            fail("item text body contains too many characters");
        } catch(IllegalArgumentException e) {

        }
    }


    /**
     * Tests if a price below zero was places for an ItemListing.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testItemListingNegativePrice() {
        new ItemListing("buik3v@uw.edu", "sdfsd", "good textbody", -11.00, new Date(), "image.jpg");
    }

    /**
     * Tests if there was a null date for an item listing.
     */
    @Test(expected = NullPointerException.class)
    public void testItemListingNullDate() {
        new ItemListing("buik3v@uw.edu", "good title", "good text body", 11.00, null, "image.jpg");
    }

    /**
     * Tests is an item listing has no image url.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testItemListingEmptyImageUrl() {
        new ItemListing("buik3v@uw.edu", "good title", "good text body", 11.00, new Date(), "");
    }

    /**
     * Tests if an item listing's email can be set.
     */
    @Test
    public void testItemListingSetEmail() {
        testItemListing.setEmail("test@uw.edu");
        assertEquals("test@uw.edu", testItemListing.getEmail());
    }


    /**
     * Tests if an item listing's title can be set.
     */
    @Test
    public void testItemListingSetTitle() {
        testItemListing.setTitle("set title");
        assertEquals("set title", testItemListing.getTitle());
    }


    /**
     * Tests if an item listings's description can be set.
     */
    @Test
    public void testItemListingSetTextBody() {
        testItemListing.setTextBody("new description");
        assertEquals("new description", testItemListing.getTextBody());
    }

    /**
     * Tests if an item listing's price can be set.
     */
    @Test
    public void testItemListingSetPrice() {
        testItemListing.setPrice(1.0);
        assertEquals((double) 1.0, (double) testItemListing.getPrice(), 0);
    }

    /**
     * Tests if an item listing's date can be set.
     */
    @Test
    public void testItemListingSetDate() {
        Calendar calender = new GregorianCalendar(2020, 6, 4);
        testItemListing.setDate(calender.getTime());
        assertEquals(new GregorianCalendar(2020, 6, 4).getTime(), testItemListing.getDate());
    }

    /**
     * Tests if an item listing's image url can be set.
     */
    @Test
    public void testItemListingSetImageUrl() {
        testItemListing.setUrl("image2.png");
        assertEquals("image2.png", testItemListing.getUrl());
    }

    /**
     * tests if an item listing's key can be set.
     */
    @Test
    public void testItemListingSetKey() {
        testItemListing.setKey("key2");
        assertEquals("key2", testItemListing.getKey());
    }

    /**
     * Tests for a null email check.
     */
    @Test
    public void testIsValidEmailNull() {
        assertFalse(ItemListing.isValidEmail(null));
    }


    /**
     * Tests an email overflows.
     */
    @Test
    public void testIsValidEmailOverflowPattern() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 300; i++) {
            sb.append("a");
        }
        sb.append("@uw.edu");
        assertFalse(ItemListing.isValidEmail(sb.toString()));
    }

    /**
     * Tests for an invalid email without an @.
     */
    @Test
    public void testIsValidEmailBadEmail() {
        assertFalse(ItemListing.isValidEmail("buik3v.edu"));
    }

    /**
     * Tests an email without a domain.
     */
    @Test
    public void testIsValidEmailNoDomain() {
        assertFalse(ItemListing.isValidEmail("buik3v@"));
    }



}
