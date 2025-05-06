package edu.uiuc.cs427app;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import org.junit.Before;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

// Tests created using ChatGpt
public class UserRegistrationTest {

    // Define test credentials
    private static final String USERNAME = "testuser2";
    private static final String PASSWORD = "testpassword2";

    // Setup intents to check if LoginActivity is started
    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testUserRegistration() throws InterruptedException {
        // Launch RegisterActivity
        ActivityScenario.launch(RegisterActivity.class);

        // Fill in the registration form
        onView(withId(R.id.inputNewUsername))
                .perform(ViewActions.typeText(USERNAME), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.inputNewPassword))
                .perform(ViewActions.typeText(PASSWORD), ViewActions.closeSoftKeyboard());

        // Select a theme from the spinner
        onView(withId(R.id.themeSpinner)).perform(ViewActions.click());
        onView(withText("Purple")).perform(ViewActions.click());

        // Click the Register button
        onView(withId(R.id.buttonRegisterUser)).perform(ViewActions.click());

        // Allow the UI to update
        Thread.sleep(1000);

        // Check if the LoginActivity is launched after registration (i.e., if the user is redirected)
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));

        Intents.release();

        // Allow the UI to update
        Thread.sleep(1000);
    }
}


