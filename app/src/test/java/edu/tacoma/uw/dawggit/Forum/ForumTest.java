/**
 * TCSS450 Spring 2020 - Sprint 2
 * Team 6
 */

package edu.tacoma.uw.dawggit.Forum;

import org.junit.Before;
import org.junit.Test;

import edu.tacoma.uw.dawggit.forum.Forum;

import static org.junit.Assert.*;

/**
 * Class for testing the forum class
 * @author Sean Smith
 * @version 6/5/2020
 */
public class ForumTest {
    /**
     * Forum being tested
     */
    Forum mForum;

    /**
     * Initalizes a forum to be tested
     */
    @Before
    public void setup() {
            mForum = new Forum("0", "Thread Title", "Thread Content", "Thread Date", "Thread User");
    }

    /**
     * Tests the getThreadID method
     */
    @Test
    public void getThreadId() {
        assertEquals("0", mForum.getThreadId());
    }

    /**
     * Tests the get title method
     */
    @Test
    public void getTitle() {
        assertEquals("Thread Title", mForum.getTitle());
    }

    /**
     * Tests the get content method
     */
    @Test
    public void getContent() {
        assertEquals("Thread Content", mForum.getContent());
    }

    /**
     * Tests the get date method
     */
    @Test
    public void getDate() {
        assertEquals("Thread Date", mForum.getDate());
    }

    /**
     * Tests the get email method
     */
    @Test
    public void getEmail() {
        assertEquals("Thread User", mForum.getEmail());
    }
}