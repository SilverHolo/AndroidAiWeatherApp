package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.IOException;
import edu.uiuc.cs427app.service.UserService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.widget.ProgressBar;

public class MapActivity extends AppCompatActivity {
    private static final String API_KEY = "a4d2fe63599127df6171dc8bf9d2a832";
    private TextView cityName, latLabel, longLabel;
    private String city;
    private double latitude;
    private double longitude;
    private String username;
    private UserService userService;
    private ProgressBar progressBar;
    private WebView mapWebView;
    private boolean isLocationMockingEnabled;
    final private double MOCK_LATITUDE = 40.1164;
    final private double MOCK_LONGITUDE = -88.2434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getIntent().getStringExtra("username");
        isLocationMockingEnabled = getIntent().getBooleanExtra("isLocationMockingEnabled", false);

        // Set theme to users saved theme
        userService = new UserService(this);
        if (username != null) {
            userService.setUserThemePreference(username);
        }

        setContentView(R.layout.map_activity);

        progressBar = findViewById(R.id.progressBar);
        cityName = findViewById(R.id.cityName);
        latLabel = findViewById(R.id.latitude);
        longLabel = findViewById(R.id.longitude);
        mapWebView = findViewById(R.id.map_webview);

        city = getIntent().getStringExtra("CITY_NAME");
        cityName.setText(city);

        // Get Latitude and Longitude data
        fetchLatLongData(city);

        // Button to go back to the main activity
        Button btnGoBackToMain = findViewById(R.id.buttonBackToMain);
        btnGoBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    /**
     * Fetches latitude and longitude for the city
     *
     * @param city Name of the city to be displayed in map and show lat long data.
     */
    private void fetchLatLongData(String city) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=imperial";

        Request request = new Request.Builder().url(url).build();

        // Call open weather API to get latitude longitude data for city
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String cityNameText = jsonObject.getString("name");
                        // Extract Latitude and Longitude data
                        latitude = jsonObject.getJSONObject("coord").getDouble("lat");
                        longitude = jsonObject.getJSONObject("coord").getDouble("lon");

                        // Mocking override
                        if (isLocationMockingEnabled) {
                            latitude = MOCK_LATITUDE;
                            longitude = MOCK_LONGITUDE;
                        }

                        displayMapFromCoords(latitude, longitude, cityNameText);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }




    /**
     *  Generates a Google Maps display of the city
     *
     * @param latitude latitude of city
     * @param longitude longitude of city
     * @param cityNameText string city name
     */
    protected void displayMapFromCoords(double latitude, double longitude, String cityNameText) {
        // Update UI on main thread
        runOnUiThread(() -> {
            cityName.setText(cityNameText);
            latLabel.setText("Latitude: " + latitude);
            longLabel.setText("Longitude: " + longitude);

            // Embed URL for Google Maps
            String mapUrl = "https://maps.google.com/maps?q=" + latitude + "," + longitude + "&t=&z=15&ie=UTF8&iwloc=&output=embed";

            // HTML to wrap the URL in an iframe
            String html = "<html><body>" +
                    "<iframe width='100%' height='100%' frameborder='0' style='border:0' src='"
                    + mapUrl + "' allowfullscreen></iframe>" +
                    "</body></html>";

            // Set up WebView
            mapWebView.getSettings().setJavaScriptEnabled(true);
            mapWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

            // Load the HTML into the WebView
            mapWebView.loadData(html, "text/html", "UTF-8");
        });
    }
}