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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

// Test created using ChatGPT
public class LoginTest {

    // Define test credentials
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpassword";

    // Setup intents to check if MainActivity or RegisterActivity is started
    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testLogin() throws InterruptedException {
        // Launch RegisterActivity
        ActivityScenario.launch(RegisterActivity.class);

        // Fill in the registration form with valid credentials
        onView(withId(R.id.inputNewUsername))
                .perform(ViewActions.typeText(USERNAME), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.inputNewPassword))
                .perform(ViewActions.typeText(PASSWORD), ViewActions.closeSoftKeyboard());

        Thread.sleep(1000);

        // Select a theme from the spinner (if necessary)
        onView(withId(R.id.themeSpinner)).perform(ViewActions.click());
        onView(withText("Purple")).perform(ViewActions.click());

        // Click the Register button
        onView(withId(R.id.buttonRegisterUser)).perform(ViewActions.click());

        // Check if the LoginActivity is launched after registration (i.e., if the user is redirected)
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));

        Thread.sleep(1000);

        // Fill in the valid credentials in log in page
        onView(withId(R.id.inputUsername)).perform(ViewActions.typeText(USERNAME), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.inputPassword)).perform(ViewActions.typeText(PASSWORD), ViewActions.closeSoftKeyboard());

        // Click the Login button
        onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

        // Allow UI to update
        Thread.sleep(1000);

        // Verify that the intent to MainActivity was fired with the correct username as extra
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        Intents.intended(IntentMatchers.hasExtra("username", USERNAME));

        Thread.sleep(1000);

        Intents.release();
    }
}
