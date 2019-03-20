package net.chaosserver.weathernext.weather.services;

import java.util.Calendar;
import java.util.TimeZone;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.zipcode.ZipCodeLookup;

import org.junit.Test;

public class ForecastIoWeatherServiceTest {
    @Test
    public void testForecastWeatherService() throws Exception {
        ForecastIoWeatherService forecastIoWeatherService = new ForecastIoWeatherService(
                new ZipCodeLookup());

        TimeZone timezone = Calendar.getInstance().getTimeZone();
        WeatherData weatherData = forecastIoWeatherService.getWeather("95608",
                timezone);

        System.out.println("Weather Service: " + weatherData);
    }
}
