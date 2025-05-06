package edu.uiuc.cs427app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
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
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static org.hamcrest.Matchers.containsString;

import android.content.Intent;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LocationTest {

    @Test
    public void testMapButtonForChicago() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Add the city "Chicago"
        String cityName = "Chicago";
        // Expected latitude and longitude for Chicago
        String expectedLatitude = "41.85";
        String expectedLongitude = "-87.65";

        // Add Chicago as a location
        onView(withId(R.id.cityNameInput)).perform(typeText(cityName), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        // Allow the UI to update
        Thread.sleep(3000);

        // Click the "MAP" button for "Chicago"
        onView(allOf(
                withText("MAP"),
                hasSibling(withText(cityName))
        )).perform(click());

        Thread.sleep(3000);

        // Validate that the MapActivity opens with the correct city name
        onView(withId(R.id.cityName)).check(matches(withText(cityName)));
        // Validate that the MapActivity opens with the correct latitude and longitude
        onView(withId(R.id.latitude)).check(matches(withText("Latitude: " + expectedLatitude)));
        onView(withId(R.id.longitude)).check(matches(withText("Longitude: " + expectedLongitude)));

        // Verify that the WebView for the map is displayed
        onView(withId(R.id.map_webview)).check(matches(isDisplayed()));

        // Check that the ProgressBar is hidden (also indicates the map has loaded)
        onView(withId(R.id.progressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Validate that the WebView for the map contains the correct map URL
        String expectedMapUrl = "https://maps.google.com/maps?q=" + expectedLatitude + "," + expectedLongitude + "&t=&z=15&ie=UTF8&iwloc=&output=embed";
        onWebView(withId(R.id.map_webview))
                .forceJavascriptEnabled()  // Enable JavaScript if not already enabled
                .check(webMatches(getCurrentUrl(), containsString(expectedMapUrl)));

        Thread.sleep(3000);
    }

    @Test
    public void testMapButtonForNewYork() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Add the city "New York"
        String cityName = "New York";
        // Expected latitude and longitude for New York
        String expectedLatitude = "40.7143";
        String expectedLongitude = "-74.006";

        // Add New York as a location
        onView(withId(R.id.cityNameInput)).perform(typeText(cityName), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(3000); // Allow the UI to update

        // Click the "MAP" button for "New York"
        onView(allOf(
                withText("MAP"),
                hasSibling(withText(cityName))
        )).perform(click());

        Thread.sleep(3000);

        // Validate that the MapActivity opens with the correct city name
        onView(withId(R.id.cityName)).check(matches(withText(cityName)));
        // Validate that the MapActivity opens with the correct latitude and longitude
        onView(withId(R.id.latitude)).check(matches(withText("Latitude: " + expectedLatitude)));
        onView(withId(R.id.longitude)).check(matches(withText("Longitude: " + expectedLongitude)));

        // Verify that the WebView for the map is displayed
        onView(withId(R.id.map_webview)).check(matches(isDisplayed()));

        // Check that the ProgressBar is hidden (also indicates the map has loaded)
        onView(withId(R.id.progressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Validate that the WebView for the map contains the correct map URL
        String expectedMapUrl = "https://maps.google.com/maps?q=" + expectedLatitude + "," + expectedLongitude + "&t=&z=15&ie=UTF8&iwloc=&output=embed";
        onWebView(withId(R.id.map_webview))
                .forceJavascriptEnabled()  // Enable JavaScript if not already enabled
                .check(webMatches(getCurrentUrl(), containsString(expectedMapUrl)));


        Thread.sleep(3000);
    }

    @Test
    public void test_location_mocking() throws InterruptedException {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Add the city "New York"
        String cityName = "New York";

        // Expected mock latitude and longitude (Champaign, IL)
        String expectedLatitude = "40.1164";
        String expectedLongtitude = "-88.2434";

        // Add New York as a location
        onView(withId(R.id.cityNameInput)).perform(typeText(cityName), closeSoftKeyboard());
        onView(withId(R.id.buttonAddLocation)).perform(click());

        Thread.sleep(3000); // Allow the UI to update


        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("isLocationMockingEnabled", true);
        intent.putExtra("CITY_NAME", cityName);

        ActivityScenario.launch(intent);

        Thread.sleep(3000);

        // Validate that the MapActivity opens with the correct city name
        onView(withId(R.id.cityName)).check(matches(withText(cityName)));

        // Validate that the MapActivity opens with the correct latitude and longitude
        onView(withId(R.id.latitude)).check(matches(withText("Latitude: " + expectedLatitude)));
        onView(withId(R.id.longitude)).check(matches(withText("Longitude: " + expectedLongtitude)));

        // Verify that the WebView for the map is displayed
        onView(withId(R.id.map_webview)).check(matches(isDisplayed()));

        // Check that the ProgressBar is hidden (also indicates the map has loaded)
        onView(withId(R.id.progressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Validate that the WebView for the map contains the correct map URL
        String expectedMapUrl = "https://maps.google.com/maps?q=" + expectedLatitude + "," + expectedLongtitude + "&t=&z=15&ie=UTF8&iwloc=&output=embed";
        onWebView(withId(R.id.map_webview))
                .forceJavascriptEnabled()  // Enable JavaScript if not already enabled
                .check(webMatches(getCurrentUrl(), containsString(expectedMapUrl)));

        Thread.sleep(5000);
    }
}