package edu.tacoma.uw.dawggit;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.tacoma.uw.dawggit.course.CourseAddActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;

/**
 * Test the add course activity.
 *
 * NOTE: APP SHOULD BE LOGGED IN BEFORE RUNNING TEST.
 * Relies on shared prefs of user email.
 *
 * @author Minh Nguyen
 * @version Sprint 2
 */
@RunWith(AndroidJUnit4.class)
public class CourseAddTest {

    @Rule
    public ActivityTestRule<CourseAddActivity> mActivityRule =
            new ActivityTestRule<>(CourseAddActivity.class);

    /**
     * Test the add course activity when there's no information provided.
     */
    @Test
    public void testAddNoInfo() {
        onView(withId(R.id.addCourse)).perform(click());

        onView(withText("Course couldn't be added"))
                .inRoot(withDecorView(not(is(mActivityRule
                        .getActivity()
                        .getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test the add course activity when there are some information provided,
     * but not enough to successfully add a course.
     * Missing course info and user email.
     */
    @Test
    public void testAddSomeInfo() {
        onView(withId(R.id.editCourseID))
                .perform(typeText("TEST100"));

        onView(withId(R.id.editCourseTitle))
                .perform(typeText("TCSS 100 Test Title"));

        onView(withId(R.id.addCourse)).perform(click());

        onView(withText("Course couldn't be added"))
                .inRoot(withDecorView(not(is(mActivityRule
                        .getActivity()
                        .getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test the add course activity when all information is provided.
     * This should successfully add a course.
     *
     * ////// NOTE: will FAIL if ran the second time because course already exist /////////
     * in the database!
     *
     * TRY DIFFERENT COURSE ID (EX: TCSS200).
     */
    @Test
    public void testAddSuccess() {
        onView(withId(R.id.editCourseID))
                .perform(typeText("TEST222"));

        onView(withId(R.id.editCourseTitle))
                .perform(typeText("TCSS 222 Test Title"));

        onView(withId(R.id.editCourseInfo))
                .perform(typeText("TCSS 222 Test Info"));

        onView(withId(R.id.addCourse)).perform(click());

        onView(withText("Course added successfully"))
                .inRoot(withDecorView(not(is(mActivityRule
                        .getActivity()
                        .getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Try to add a course that already exist.
     * Course should not be added.
     */
    @Test
    public void testCourseExisted() {
        onView(withId(R.id.editCourseID))
                .perform(typeText("TCSS000"));

        onView(withId(R.id.editCourseTitle))
                .perform(typeText("TCSS 000 Test Title"));

        onView(withId(R.id.editCourseInfo))
                .perform(typeText("TCSS 000 Test Info"));

        onView(withId(R.id.addCourse)).perform(click());

        onView(withText("Course couldn't be added"))
                .inRoot(withDecorView(not(is(mActivityRule
                        .getActivity()
                        .getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Check the close button. The activity should be finished.
     */
    @Test
    public void testCloseButton() {
        onView(withId(R.id.closeAddCourse)).perform(click());

        assertTrue(mActivityRule.getActivity().isFinishing());
    }
}
