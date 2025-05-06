package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.room.Room;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs427app.service.CityService;
import edu.uiuc.cs427app.service.UserService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout locationsLayout;
    private EditText cityNameInput;
    private String username;
    private AppDatabase db;
    private List<String> cityList;
    private UserService userService;
    private TextView emptyCityListText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the username
        username = getIntent().getStringExtra("username");

        // Initialize UserService and set the theme
        userService = new UserService(this);
        if (username != null) {
            userService.setUserThemePreference(username);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show the username on the main screen title
        if (username != null) {
            setTitle(getTitle().toString() + " - " + username);
        }

        cityList = new ArrayList<>();
        locationsLayout = findViewById(R.id.locationsLayout);
        cityNameInput = findViewById(R.id.cityNameInput);
        emptyCityListText = findViewById(R.id.emptyCityListText);

        // Initialize the database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class,
                        "city-database").allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        loadCities();

        // Initialize UI components
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonLogout = findViewById(R.id.buttonLogOut);

        buttonNew.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    /**
     * Loads cities from the database for the logged-in user.
     * Adds each city to the UI list dynamically.
     */
    private void loadCities() {
        List<City> cities = db.cityDao().getCitiesForUser(username);
        for (City city : cities)  {
            addCityToList(city.cityName);
        }

        // Show default text if list is empty
        if (cities.isEmpty()) {
            emptyCityListText.setVisibility(View.VISIBLE);
        }
        else {
            emptyCityListText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Inserts a new city into the Room database for the logged-in user.
     *
     * @param cityName Name of the city to be added.
     */
    private void addCityToDB(String cityName) {
        db.cityDao().insertCity(new City(cityName, username));
    }

    /**
     * Removes a city from the Room database for the logged-in user.
     *
     * @param cityName Name of the city to be removed.
     */
    private void removeCityFromDB(String cityName) {
        db.cityDao().DeleteCity(username, cityName);
    }

    /**
     * Adds a city to the UI list with DELETE and SHOW WEATHER buttons.
     *
     * @param cityName Name of the city to be displayed.
     */
    private void addCityToList(String cityName) {
        cityList.add(cityName);

        LinearLayout cityLayout = new LinearLayout(this);
        cityLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("DELETE");
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // TextView for city name
        TextView cityTextView = new TextView(this);
        cityTextView.setText(cityName);
        cityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // SHOW WEATHER button
        Button weatherButton = new Button(this);
        weatherButton.setText("WEATHER");
        weatherButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // SHOW MAP button
        Button mapButton = new Button(this);
        mapButton.setText("MAP");
        mapButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add views to city layout
        cityLayout.addView(deleteButton);
        cityLayout.addView(cityTextView);
        cityLayout.addView(weatherButton);
        cityLayout.addView(mapButton);

        // Add city layout to locations layout
        locationsLayout.addView(cityLayout);

        // Set OnClickListener to delete the city on button click
        deleteButton.setOnClickListener(v -> {
            locationsLayout.removeView(cityLayout);
            removeCityFromDB(cityName);
            cityList.remove(cityName);
        });

        // Set OnClickListener to show weather details for the city
        weatherButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            intent.putExtra("CITY_NAME", cityName);
            intent.putExtra("username", username);  // Pass username to WeatherActivity
            startActivity(intent);
        });

        // Set OnClickListener to show weather details for the city
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.putExtra("CITY_NAME", cityName);
            intent.putExtra("username", username);  // Pass username to WeatherActivity
            startActivity(intent);
        });

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonLogOut:
                // Redirect to login page on logout
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonAddLocation:
                String cityName = cityNameInput.getText().toString();

                // Check against cities already on the list
                if (cityList.contains(cityName)) {
                    Toast.makeText(this, cityName + " is already on the list!", Toast.LENGTH_SHORT).show();
                    break;
                }

                // Validate if the input is a real city and show a toast message accordingly
                CityService cityService = new CityService();
                cityService.doesCityExist(cityName, exists -> runOnUiThread(() -> {
                    if (exists) {
                        addCityToList(cityName);  // Add the city to the list
                        addCityToDB(cityName);
                        Toast.makeText(this, cityName + " is added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, cityName + " doesn't exist!", Toast.LENGTH_SHORT).show();
                    }
                }));
                break;
        }
    }
}
