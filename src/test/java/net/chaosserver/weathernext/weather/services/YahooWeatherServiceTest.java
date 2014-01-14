package net.chaosserver.weathernext.weather.services;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class YahooWeatherServiceTest {
	public void textYahooWeatherService() throws Exception {
		YahooWeatherService yahooWeatherService = new YahooWeatherService();
		TimeZone timezone = Calendar.getInstance().getTimeZone();
		yahooWeatherService.getWeather("95608", timezone);
	}
}
