package edu.uiuc.cs427app.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service class to check if a city exists by calling the OpenWeatherMap API.
 */
public class CityService {
    private static final String API_KEY = "31044b4158bae3104a6b8397db5bbca6";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?appid=%s&q=%s&units=metric";

    /**
     * Asynchronously checks if the given city exists using OpenWeatherMap API.
     * Runs the network request on a separate thread and invokes the callback upon completion.
     *
     * @param cityName The name of the city to check.
     * @param callback Callback to handle the result (whether the city exists or not).
     */
    public void doesCityExist(String cityName, CityExistCallback callback) {
        new Thread(() -> {
            boolean result = checkCity(cityName);
            callback.onCityExistCheck(result);
//            System.out.println(result);
        }).start();
    }

    /**
     * Makes a synchronous HTTP GET request to the OpenWeatherMap API to verify the existence of the city.
     *
     * @param cityName The name of the city to check.
     * @return True if the city exists, false otherwise.
     */
    private boolean checkCity(String cityName) {
        System.out.println(cityName);
        cityName = cityName.replace(" ", "%20");
        String urlString = String.format(API_URL, API_KEY, cityName);
        System.out.println(urlString);
        try {

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            System.err.println("Error in network call: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Interface to handle the result of the city existence check asynchronously.
     */
    public interface CityExistCallback {
        void onCityExistCheck(boolean exists);
    }
}

