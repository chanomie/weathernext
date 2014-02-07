package net.chaosserver.weathernext.weather.services;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.weather.WeatherService;
import net.chaosserver.weathernext.weather.WeatherState;

public class StaticWeatherService implements WeatherService {
    public WeatherData getWeather(String zipcode, TimeZone timeZone) {

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 55);
        Date sunrise = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 22);
        Date sunset = calendar.getTime();

        WeatherData weatherData = new WeatherData(tomorrow, "Carmichael",
                WeatherState.CLEAR, "Mostly Sunny", 68.0f, 36.0f, sunrise,
                sunset, "Static Weather Service", null);

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        weatherData.addForecast(new WeatherData(calendar.getTime(),
                "Carmichael", WeatherState.CLEAR, "Sunny", 66.0f, 36.0f, null,
                null, "Static Weather Service", null));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        weatherData.addForecast(new WeatherData(calendar.getTime(),
                "Carmichael", WeatherState.CLEAR, "Sunny", 66.0f, 36.0f, null,
                null, "Static Weather Service", null));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        weatherData.addForecast(new WeatherData(calendar.getTime(),
                "Carmichael", WeatherState.CLOUDS, "Mostly Cloudy", 64.0f,
                41.0f, null, null, "Static Weather Service", null

        ));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        weatherData.addForecast(new WeatherData(calendar.getTime(),
                "Carmichael", WeatherState.CLOUDS, "Cloudy", 66.0f, 37.0f,
                null, null, "Static Weather Service", null

        ));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        weatherData.addForecast(new WeatherData(calendar.getTime(),
                "Carmichael", WeatherState.CLEAR, "Sunny", 66.0f, 36.0f, null,
                null, "Static Weather Service", null

        ));

        return weatherData;
    }

}
