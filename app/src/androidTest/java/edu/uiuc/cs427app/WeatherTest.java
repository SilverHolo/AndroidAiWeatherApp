package edu.uiuc.cs427app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumented test for weather feature, validating weather data for two cities.
 */
@RunWith(AndroidJUnit4.class)
public class WeatherTest {

    @Test
    public void testWeatherForChicago() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Add the city "Chicago"
        String cityName = "Chicago";

        // Add Chicago to the list
        onView(withId(R.id.cityNameInput)).perform(typeText(cityName), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(3000); // Allow UI to update

        // Click the "WEATHER" button for "Chicago"
        onView(allOf(
                withText("WEATHER"),
                hasSibling(withText(cityName))
        )).perform(click());

        Thread.sleep(3000); // Allow weather data to load

        // Validate that the WeatherActivity opens with the correct city name
        onView(withId(R.id.cityName)).check(matches(withText(cityName)));
        // Validate weather details are displayed
        onView(withId(R.id.temperature)).check(matches(isDisplayed()));
        onView(withId(R.id.weatherCondition)).check(matches(isDisplayed()));
        onView(withId(R.id.humidity)).check(matches(isDisplayed()));
        onView(withId(R.id.windCondition)).check(matches(isDisplayed()));
    }

    @Test
    public void testWeatherForNewYork() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Add the city "New York"
        String cityName = "New York";

        // Add New York to the list
        onView(withId(R.id.cityNameInput)).perform(typeText(cityName), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(3000); // Allow UI to update

        // Click the "WEATHER" button for "New York"
        onView(allOf(
                withText("WEATHER"),
                hasSibling(withText(cityName))
        )).perform(click());

        Thread.sleep(3000); // Allow weather data to load

        // Validate that the WeatherActivity opens with the correct city name and tests
        onView(withId(R.id.cityName)).check(matches(withText(cityName)));
        // Validate weather details are displayed
        onView(withId(R.id.temperature)).check(matches(isDisplayed()));
        onView(withId(R.id.weatherCondition)).check(matches(isDisplayed()));
        onView(withId(R.id.humidity)).check(matches(isDisplayed()));
        onView(withId(R.id.windCondition)).check(matches(isDisplayed()));
    }
}
