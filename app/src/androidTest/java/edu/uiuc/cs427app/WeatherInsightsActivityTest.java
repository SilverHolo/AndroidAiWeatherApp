package edu.uiuc.cs427app;

import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WeatherInsightsActivityTest {

    @Test
    public void test_displayQuestions_should_create_buttons() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WeatherInsightsActivity.class);
        intent.putExtra("CITY_NAME", "Test City");
        intent.putExtra("WEATHER_DATA", "Sunny with a chance of rain.");
        ActivityScenario<WeatherInsightsActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            LinearLayout questionContainer = activity.findViewById(R.id.questionContainer);
            assertNotNull(questionContainer);
            assertEquals(0, questionContainer.getChildCount());

            // Pause to allow visual display of no buttons
            sleep(2000);

            // Simulate the display of questions
            activity.displayQuestions("Question 1\nQuestion 2");

            // Pause to allow visual display of buttons
            sleep(2000);

            assertEquals(2, questionContainer.getChildCount());

            // Verify the buttons and their text
            Button question1Button = (Button) questionContainer.getChildAt(0);
            Button question2Button = (Button) questionContainer.getChildAt(1);
            assertEquals("Question 1", question1Button.getText().toString());
            assertEquals("Question 2", question2Button.getText().toString());
        });
    }

    @Test
    public void test_question_button_click_should_show_answer_text() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WeatherInsightsActivity.class);
        intent.putExtra("CITY_NAME", "Test City");
        intent.putExtra("WEATHER_DATA", "Sunny with a chance of rain.");
        ActivityScenario<WeatherInsightsActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            TextView answerText = activity.findViewById(R.id.answerText);
            assertNotNull(answerText);
            assertEquals(ViewMatchers.Visibility.INVISIBLE.getValue(), answerText.getVisibility());
            assertEquals("", answerText.getText());

            activity.displayQuestions("Question 1\nQuestion 2");

            LinearLayout questionContainer = activity.findViewById(R.id.questionContainer);
            Button question1Button = (Button) questionContainer.getChildAt(0);

            question1Button.performClick();

            assertEquals(ViewMatchers.Visibility.VISIBLE.getValue(), answerText.getVisibility());
        });
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}

