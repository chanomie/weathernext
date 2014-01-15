package net.chaosserver.weathernext.weather.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.weather.WeatherService;
import net.chaosserver.weathernext.weather.WeatherState;
import net.chaosserver.weathernext.zipcode.GeoCoord;
import net.chaosserver.weathernext.zipcode.ZipCodeLookup;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

public class ForecastIoWeatherService implements WeatherService {
	/** The Forecast.io API key loaded from the context. */
	protected static String apiKey = System.getenv("forecastkey");
	
	/** Logger. */
	private final static Logger logger = Logger.getLogger(ForecastIoWeatherService.class.getName()); 

	protected ZipCodeLookup zipCodeLookup;

	public ForecastIoWeatherService(ZipCodeLookup zipCodeLookup) {
		this.zipCodeLookup = zipCodeLookup;

		if(apiKey == null) {
			apiKey = System.getProperty("forecastkey");
			if(apiKey == null) {
				throw new IllegalStateException("Failed to get forecastkey from environment");
			}
		}
		
	}
	

	@Override
	public WeatherData getWeather(String zipcode, TimeZone timeZone) {
		WeatherData result = null;
		GeoCoord geoCoord = zipCodeLookup.getGeoCoord(zipcode);

		if(geoCoord != null) {
			Calendar calendar = Calendar.getInstance(timeZone);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date todayEnd = calendar.getTime();
			
			try {
	
				String forecastURLString = "https://api.forecast.io/forecast/"
						+ apiKey + "/" + geoCoord.getLatitude() + ","
						+ geoCoord.getLongitude() + "?excudes=currently,minutely,hourly,alerts,flags";
				
				URL forecastURL = new URL(forecastURLString);
				ObjectMapper mapper = new ObjectMapper();
				JsonFactory factory = mapper.getJsonFactory();
				JsonParser jp = factory.createJsonParser(
						 new BufferedInputStream(forecastURL.openStream())
						);
				
				JsonNode actualObj = mapper.readTree(jp);
				
				JsonNode resultNode = actualObj.path("daily").path("data");
				if(resultNode instanceof ArrayNode) {
					Iterator<JsonNode> dataNodesIterator = ((ArrayNode)resultNode).getElements();
					while(dataNodesIterator.hasNext()) {
						JsonNode dataNode = dataNodesIterator.next();
						
						long dateLong = dataNode.path("time").getLongValue();
						double low = dataNode.path("temperatureMin").getDoubleValue();
						double high = dataNode.path("temperatureMax").getDoubleValue();
						String description = (String) dataNode.path("summary").getTextValue();
						String iconCode = (String) dataNode.path("icon").getTextValue();
						long sunriseLong = dataNode.path("sunriseTime").getLongValue();
						long sunsetLong = dataNode.path("sunsetTime").getLongValue();
						
						Date date = new Date(dateLong * 1000);
						Date sunrise = new Date(sunriseLong * 1000);
						Date sunset = new Date(sunsetLong * 1000);
						WeatherState weatherState = parseWeatherState(iconCode);
	
						if(date.after(todayEnd)) {
							WeatherData weatherData = new WeatherData(
										date, 
										null, 
										weatherState,
										description,
										(float)high,
										(float)low,
										sunrise,
										sunset);
							
							if(logger.isLoggable(Level.FINEST)) {
								logger.finest("Parsed iconCode [" + iconCode + "] include resultstate [" + weatherState + "]"
										+ " and added : " + weatherData);
							}
	
							
							if(result == null) {
								result = weatherData;
							} else {
								result.addForecast(weatherData);
							}
						}
						
						
					}
				}
				
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to properly load weather due to exception", e);
			}
		}
		
		return result;
	}

	private WeatherState parseWeatherState(String iconCode) {
		WeatherState resultState = WeatherState.UNKNOWN;
		if("clear-day".equals(iconCode)
				|| "clear-night".equals(iconCode)
				|| "wind".equals(iconCode)) {
			
			resultState = WeatherState.CLEAR;
		} else if ("rain".equals(iconCode)) {
			resultState = WeatherState.RAIN;
		} else if ("cloudy".equals(iconCode)
				|| "partly-cloudy-day".equals(iconCode)
				|| "partly-cloudy-night".equals(iconCode)) {

			resultState =WeatherState.CLOUDS;
		} else if ("snow".equals(iconCode)
				|| "sleet".equals(iconCode)) {
			
			resultState = WeatherState.SNOW;
		} else if ("fog".equals(iconCode)) {
			resultState = WeatherState.ATMOSPHERE;
		}
			
		/*
				resultState = WeatherState.THUNDERSTORM;
				resultState = WeatherState.DRIZZLE;
				resultState = WeatherState.EXTREME;
				resultState = WeatherState.UNKNOWN;
		*/
				
		return resultState;
	}
	
	
}