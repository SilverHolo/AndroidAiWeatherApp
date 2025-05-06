package edu.uiuc.cs427app;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddNewValidCity() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);
        // Check that the input field and the button to add a city are visible
        onView(withId(R.id.cityNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonAddLocation)).check(matches(isDisplayed()));

        // Add a new valid city
        String validCity = "Chicago";
        onView(withId(R.id.cityNameInput)).perform(typeText(validCity), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(3000); // Temporary delay to allow the view to load

        // Validate the city is added properly
        onView(allOf(
                withParent(withId(R.id.locationsLayout)),
                hasDescendant(withText(validCity))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testAddNewInvalidCity() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Add a new invalid city
        String invalidCity = "ThisIsNotACityName";
        onView(withId(R.id.cityNameInput)).perform(typeText(invalidCity), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(3000); // Temporary delay to allow the view to load

        // Validate the city is not added to the list
        onView(allOf(
                withParent(withId(R.id.locationsLayout)),
                hasDescendant(withText(invalidCity))
        )).check(doesNotExist());
    }

    @Test
    public void testRemoveCity() throws InterruptedException {
        // Add a city
        onView(withId(R.id.cityNameInput)).perform(typeText("Chicago"));
        onView(withId(R.id.buttonAddLocation)).perform(click());

        // Verify the city is added
        Thread.sleep(3000); // Temporary delay to allow the view to load
        onView(allOf(
                withParent(withId(R.id.locationsLayout)),
                hasDescendant(withText("Chicago"))
        )).check(matches(isDisplayed()));

        // Click the DELETE button for "Chicago"
        onView(allOf(withText("DELETE"), hasSibling(withText("Chicago")))).perform(click());
        // Verify the city is removed
        onView(allOf(
                withParent(withId(R.id.locationsLayout)),
                hasDescendant(withText("Chicago"))
        )).check(doesNotExist());
    }

    @Test
    public void testUserLogOff() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);

        // Verify there is a log out button
        onView(withId(R.id.buttonLogOut)).check(matches(isDisplayed()));
        Thread.sleep(3000);
        onView(withId(R.id.buttonLogOut)).perform(click());
        Thread.sleep(3000);

        // Clicking to the log out button should return to the user log in page
        onView(withId(R.id.inputUsername)).check(matches(isDisplayed()));
        onView(withId(R.id.inputPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }
}