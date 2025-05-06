package edu.uiuc.cs427app;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import java.util.logging.Logger;


public class WeatherManager {

    private final String weatherURL = "https://api.openweathermap.org/data/2.5/weather?appid=31044b4158bae3104a6b8397db5bbca6";
    private WeatherManagerDelegate delegate;

//    public WeatherManager(WeatherManagerDelegate delegate) {
//        this.delegate = delegate;
//    }

    public void fetchWeather(String cityName) {
        String urlString = weatherURL + "&q=" + cityName;
        performRequest(urlString);
    }

    public void fetchWeather(double latitude, double longitude) {
        String urlString = weatherURL + "&lat=" + latitude + "&lon=" + longitude;
        performRequest(urlString);
    }

//    Thread worker
    public boolean validRequest(String cityName) {
        String urlString = weatherURL + "&q=" + cityName + "&units=metric";
        try {
            URL url = new URL(urlString);
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    private void performRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                parseJSON(content.toString());
            } else {
                delegate.didFailWithError(new Exception("HTTP error code: " + responseCode));
            }
        } catch (Exception e) {
            delegate.didFailWithError(e);
        }
    }

    private void parseJSON(String weatherData) {
        try {
            JSONObject jsonObject = new JSONObject(weatherData);
            int id = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            double temp = jsonObject.getJSONObject("main").getDouble("temp");
            String name = jsonObject.getString("name");

            WeatherModel weather = new WeatherModel(id, name, temp);
            delegate.didUpdateWeather(this, weather);

        } catch (Exception e) {
            delegate.didFailWithError(e);
        }
    }

    // Delegate interface
    public interface WeatherManagerDelegate {
        void didUpdateWeather(WeatherManager weatherManager, WeatherModel weather);
        void didFailWithError(Exception error);
    }

    // WeatherModel class
    public static class WeatherModel {
        private int conditionId;
        private String cityName;
        private double temperature;

        public WeatherModel(int conditionId, String cityName, double temperature) {
            this.conditionId = conditionId;
            this.cityName = cityName;
            this.temperature = temperature;
        }

        public int getConditionId() {
            return conditionId;
        }

        public String getCityName() {
            return cityName;
        }

        public double getTemperature() {
            return temperature;
        }
    }
}