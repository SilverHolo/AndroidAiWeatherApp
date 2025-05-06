package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.Part;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.concurrent.Executors;
import org.checkerframework.checker.nullness.qual.Nullable;
import android.view.View;

public class WeatherInsightsActivity extends AppCompatActivity {

    private static final String MODEL_NAME = "gemini-1.5-flash";
    private LinearLayout questionContainer;
    private TextView answerText;
    private String weatherData;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_insights);

        questionContainer = findViewById(R.id.questionContainer);
        answerText = findViewById(R.id.answerText);
        TextView titleText = findViewById(R.id.titleText);

        cityName = getIntent().getStringExtra("CITY_NAME");
        weatherData = getIntent().getStringExtra("WEATHER_DATA");

        if (cityName != null) {
            titleText.setText(cityName);
        }

        generateQuestions(weatherData);

        // Back button to return to WeatherActivity
        Button btnGoBackToWeather = findViewById(R.id.buttonBackToWeather);
        btnGoBackToWeather.setOnClickListener(v -> {
            Intent intent = new Intent(WeatherInsightsActivity.this, WeatherActivity.class);
            intent.putExtra("CITY_NAME", cityName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void generateQuestions(String weatherData) {
        GenerativeModel model = new GenerativeModel(MODEL_NAME, BuildConfig.GEMINI_API_KEY);
        Content promptContent = new Content.Builder()
                .addText("Today's weather is: " + weatherData + ". Please generate two context-specific questions.")
                .build();

        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(model);
        ListenableFuture<GenerateContentResponse> response = modelFutures.generateContent(promptContent);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(@Nullable GenerateContentResponse result) {
                if (result != null && !result.getCandidates().isEmpty()) {
                    List<Part> parts = result.getCandidates().get(0).getContent().getParts();

                    if (!parts.isEmpty()) {
                        StringBuilder questionsTextBuilder = new StringBuilder();
                        for (Part part : parts) {
                            try {
                                String textContent = part.getClass().getMethod("getText").invoke(part).toString();
                                questionsTextBuilder.append(textContent).append("\n");
                            } catch (Exception e) {
                                Log.e("WeatherInsightsActivity", "Error accessing text in part", e);
                            }
                        }

                        String questionsText = questionsTextBuilder.toString();
                        runOnUiThread(() -> displayQuestions(questionsText));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("WeatherInsightsActivity", "API Request Failed", t);
                runOnUiThread(() -> Toast.makeText(WeatherInsightsActivity.this, "Error generating questions", Toast.LENGTH_SHORT).show());
            }
        }, Executors.newSingleThreadExecutor());
    }

    void displayQuestions(String questionsText) {
        questionContainer.removeAllViews();

        // Splits questions by newline and adds up to two buttons
        String[] questions = questionsText.split("\n");
        int questionCount = 0;

        for (String question : questions) {
            if (!question.trim().isEmpty() && questionCount < 2) {
                questionCount++;

                Button questionButton = new Button(this);
                questionButton.setText(question.trim());
                questionButton.setOnClickListener(v -> {
                    // Update the answer text
                    answerText.setVisibility(View.VISIBLE);
                    getAnswerForQuestion(question.trim());
                });

                // Add the button to the container
                questionContainer.addView(questionButton);
            }
        }
    }

    private void getAnswerForQuestion(String question) {
        GenerativeModel model = new GenerativeModel(MODEL_NAME, BuildConfig.GEMINI_API_KEY);
        Content promptContent = new Content.Builder()
                .addText("Based on the weather data: '" + weatherData + "', answer the question: '" + question + "'")
                .build();

        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(model);
        ListenableFuture<GenerateContentResponse> response = modelFutures.generateContent(promptContent);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(@Nullable GenerateContentResponse result) {
                if (result != null && !result.getCandidates().isEmpty()) {
                    StringBuilder answerBuilder = new StringBuilder();
                    List<Part> parts = result.getCandidates().get(0).getContent().getParts();
                    for (Part part : parts) {
                        try {
                            String textContent = part.getClass().getMethod("getText").invoke(part).toString();
                            answerBuilder.append(textContent).append("\n");
                        } catch (Exception e) {
                            Log.e("WeatherInsightsActivity", "Error accessing text in answer part", e);
                        }
                    }
                    String answer = answerBuilder.toString();
                    Log.d("WeatherInsightsActivity", "Generated Answer: " + answer);
                    runOnUiThread(() -> answerText.setText(answer));
                    generateQuestions(weatherData);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("WeatherInsightsActivity", "Answer Request Failed", t);
                runOnUiThread(() -> Toast.makeText(WeatherInsightsActivity.this, "Error retrieving answer", Toast.LENGTH_SHORT).show());
            }
        }, Executors.newSingleThreadExecutor());
    }
}
