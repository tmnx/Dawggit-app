/**
 * TCSS450 Spring 2020 - Sprint 2
 * Team 6
 */

package edu.tacoma.uw.dawggit;

import org.junit.Before;
import org.junit.Test;

import edu.tacoma.uw.dawggit.course.Course;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A simple testing of the Course class.
 *
 * @author Minh Nguyen
 * @version Sprint 2
 */
public class CourseTest {

    private Course mCourse;

    /**
     * Initialize course for testing.
     */
    @Before
    public void testCourse() {
        mCourse = new Course("TCSSXXX", "Test course xxx",
                            "Test course xxx content.", "tmn1014@uw.edu");
    }

    /**
     * Test the Course constructor.
     */
    @Test
    public void testCourseConstructor() {
        assertNotNull(new Course("TCSS001", "Test Course 001",
                                 "Test course 001 content.", "tmn1014@uw.edu"));
    }

    /**
     * Test the get method to get the course ID.
     */
    @Test
    public void testGetCourseID() {
        assertEquals("TCSSXXX", mCourse.getCourse_code());
    }

    /**
     * Test the get method to get the course title.
     */
    @Test
    public void testGetCourseTitle() {
        assertEquals("Test course xxx", mCourse.getTitle());
    }

    /**
     * Test the get method to get the course information.
     */
    @Test
    public void testGetCourseInfo() {
        assertEquals("Test course xxx content.", mCourse.getCourse_info());
    }

    /**
     * Test the get method to get the course information.
     */
    @Test
    public void testGetCourseEmail() {
        assertEquals("tmn1014@uw.edu", mCourse.getEmail());
    }

}
