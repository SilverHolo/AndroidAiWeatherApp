package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import edu.uiuc.cs427app.service.UserService;

public class WeatherActivity extends AppCompatActivity {

    private static final String API_KEY = "a4d2fe63599127df6171dc8bf9d2a832";
    private TextView cityName, dateTime, temperature, weatherCondition, humidity, windCondition;
    private String city;
    private String weatherData;
    private String username;
    private UserService userService;
    
    /**
     * Initializes the WeatherActivity, sets up UI components, and fetches weather data for the selected city.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the username from the intent
        username = getIntent().getStringExtra("username");

        // Initialize UserService and set the theme based on username
        userService = new UserService(this);
        if (username != null) {
            userService.setUserThemePreference(username);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityName = findViewById(R.id.cityName);
        dateTime = findViewById(R.id.dateTime);
        temperature = findViewById(R.id.temperature);
        weatherCondition = findViewById(R.id.weatherCondition);
        humidity = findViewById(R.id.humidity);
        windCondition = findViewById(R.id.windCondition);

        city = getIntent().getStringExtra("CITY_NAME");
        cityName.setText(city);

        fetchWeatherData(city);

        Button btnWeatherInsights = findViewById(R.id.btnWeatherInsights);
        btnWeatherInsights.setOnClickListener(v -> {
            Intent intent = new Intent(WeatherActivity.this, WeatherInsightsActivity.class);
            intent.putExtra("WEATHER_DATA", weatherData);  // Pass weather data to the new activity
            intent.putExtra("CITY_NAME", cityName.getText().toString());
            intent.putExtra("username", username);  // Pass username to WeatherInsightsActivity
            startActivity(intent);
        });

        // Button to go back to the main activity
        Button btnGoBackToMain = findViewById(R.id.buttonBackToMain);
        btnGoBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
            intent.putExtra("username", username);  // Pass username back to MainActivity
            startActivity(intent);
        });
    }

    /**
     * Fetches weather data from the OpenWeatherMap API for the given city and updates the UI.
     *
     * @param city Name of the city for which to fetch weather data.
     */
    private void fetchWeatherData(String city) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=imperial";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            /**
             * Handles the failure of the API request by logging the error.
             *
             * @param call     The Call object representing the API request.
             * @param e        The IOException that occurred during the API call.
             */
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            /**
             * Handles the successful response of the API request, parses the JSON data,
             * and updates the UI with weather information.
             *
             * @param call     The Call object representing the API request.
             * @param response The Response object containing the API response data.
             * @throws IOException If there is an error reading the response body.
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);

                        // Extract weather data
                        String cityNameText = jsonObject.getString("name");
                        double temp = jsonObject.getJSONObject("main").getDouble("temp");
                        String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                        int humidityValue = jsonObject.getJSONObject("main").getInt("humidity");
                        double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed"); // Wind speed in mph

                        // Sets date and time in U.S. format (MM/dd/yyyy hh:mm a)
                        int timezoneOffsetSeconds = jsonObject.getInt("timezone");
                        String dateTimeText = getLocalTime(timezoneOffsetSeconds);

                        // Store weather data for Gemini API
                        weatherData = "City: " + cityNameText + ", Temperature: " + temp + "°F, " +
                                "Condition: " + weather + ", Humidity: " + humidityValue + "%, " +
                                "Wind Speed: " + windSpeed + " mph";

                        // Update UI on main thread
                        runOnUiThread(() -> {
                            cityName.setText(cityNameText);
                            dateTime.setText("Date and Time (local): " + dateTimeText);
                            temperature.setText("Temperature: " + temp + "°F");
                            weatherCondition.setText("Weather: " + weather);
                            humidity.setText("Humidity: " + humidityValue + "%");
                            windCondition.setText("Wind: " + windSpeed + " mph");
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    /**
     * Converts the given timezone offset in seconds to a formatted local time string.
     *
     * @param timezoneOffsetSeconds The timezone offset from UTC in seconds.
     * @return The local time as a formatted string in the format MM/dd/yyyy hh:mm a.
     */
    private String getLocalTime(int timezoneOffsetSeconds) {
        long currentTimeMillis = System.currentTimeMillis();
        long adjustedTimeMillis = currentTimeMillis + (timezoneOffsetSeconds * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTimeText = formatter.format(new Date(adjustedTimeMillis));
        System.out.println("Converted Time: " + dateTimeText);
        return dateTimeText;
    }
}


