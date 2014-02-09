/*
 * Copyright (c) 2013 Jordan Reed 
 * 
 * This file is part of Weather.Next.
 * 
 * Weather.Next is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Weather.Next is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Weather.Next.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.chaosserver.weathernext.weather.services;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.weather.WeatherService;
import net.chaosserver.weathernext.weather.WeatherState;

/**
 * Good for testing, StaticWeatherService just provides static values
 * so that you can test.
 * 
 * @author jreed
 *
 */
public class StaticWeatherService implements WeatherService {
    /**
     * Gets the weather for the zipcode by randomly generating weather.
     * 
     * @param zipcode a zipcode
     * @param timeZone the timezone to return from
     * @return a random block of weatherdata
     */
    public WeatherData getWeather(String zipcode, TimeZone timeZone) {
    	// CHECKSTYLE:OFF
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
        // CHECKSTYLE:ON
        return weatherData;
    }

}
