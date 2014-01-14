package net.chaosserver.weathernext.weather;

import java.util.TimeZone;

public interface WeatherService {
	public WeatherData getWeather(String zipcode, TimeZone timeZone);
}
