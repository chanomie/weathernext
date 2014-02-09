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

/**
 * Uses Forecast.io as the data service for getting weather information.
 * 
 * @author jreed
 */
public class ForecastIoWeatherService implements WeatherService {
    /** The Forecast.io API key loaded from the context. */
    private static String apiKey = System.getenv("forecastkey");

    /** Number of millis in seconds. */
    private static final int MILLIS_TO_SECS = 1000;

    static {
        if (ForecastIoWeatherService.apiKey == null) {
            ForecastIoWeatherService.apiKey = System.getProperty("forecastkey");
            if (ForecastIoWeatherService.apiKey == null) {
                throw new IllegalStateException(
                        "Failed to get forecastkey from environment");
            }
        }
    }

    /** Logger. */
    private static final Logger logger = Logger
            .getLogger(ForecastIoWeatherService.class.getName());

    /** Zip code service. */
    protected ZipCodeLookup zipCodeLookup;

    /** Attribution String. */
    protected String attributionString = "Powered by Forecast";

    /** Attribute URL. */
    protected String attributionUrl = "http://forecast.io/";

    /** Creates the services and provides a zipcode lookup class.
     * 
     * @param zipCodeLookup instance of zipcode lookup
     */
    public ForecastIoWeatherService(ZipCodeLookup zipCodeLookup) {
        this.zipCodeLookup = zipCodeLookup;
    }

    @Override
    public WeatherData getWeather(String zipcode, TimeZone timeZone) {
        WeatherData result = null;
        GeoCoord geoCoord = zipCodeLookup.getGeoCoord(zipcode);

        if (geoCoord != null) {
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date todayEnd = calendar.getTime();

            try {

                String forecastURLString = "https://api.forecast.io/forecast/"
                        + apiKey + "/" + geoCoord.getLatitude() + ","
                        + geoCoord.getLongitude()
                        + "?excudes=currently,minutely,hourly,alerts,flags";

                URL forecastURL = new URL(forecastURLString);
                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getJsonFactory();
                JsonParser jp = factory
                        .createJsonParser(new BufferedInputStream(forecastURL
                                .openStream()));

                JsonNode actualObj = mapper.readTree(jp);

                JsonNode resultNode = actualObj.path("daily").path("data");
                if (resultNode instanceof ArrayNode) {
                    Iterator<JsonNode> dataNodesIterator = 
                            ((ArrayNode) resultNode).getElements();

                    while (dataNodesIterator.hasNext()) {
                        JsonNode dataNode = dataNodesIterator.next();

                        long dateLong = dataNode.path("time").getLongValue();
                        double low = dataNode.path("temperatureMin")
                                .getDoubleValue();
                        double high = dataNode.path("temperatureMax")
                                .getDoubleValue();
                        String description = (String) dataNode.path("summary")
                                .getTextValue();
                        String iconCode = (String) dataNode.path("icon")
                                .getTextValue();
                        long sunriseLong = dataNode.path("sunriseTime")
                                .getLongValue();
                        long sunsetLong = dataNode.path("sunsetTime")
                                .getLongValue();

                        Date date = new Date(dateLong * MILLIS_TO_SECS);
                        Date sunrise = new Date(sunriseLong * MILLIS_TO_SECS);
                        Date sunset = new Date(sunsetLong * MILLIS_TO_SECS);
                        WeatherState weatherState = parseWeatherState(iconCode);

                        if (date.after(todayEnd)) {
                            WeatherData weatherData = new WeatherData(date,
                                    null, weatherState, description,
                                    (float) high, (float) low, sunrise, sunset,
                                    attributionString, attributionUrl);

                            if (logger.isLoggable(Level.FINEST)) {
                                logger.finest("Parsed iconCode [" + iconCode
                                        + "] include resultstate ["
                                        + weatherState + "]" + " and added : "
                                        + weatherData);
                            }

                            if (result == null) {
                                result = weatherData;
                            } else {
                                result.addForecast(weatherData);
                            }
                        }

                    }
                }

            } catch (IOException e) {
                logger.log(Level.WARNING,
                        "Failed to properly load weather due to exception", e);
            }
        }

        return result;
    }

    /**
     * Parses the iconCode returned from the API can converts it into
     * the WeatherState enumeration.
     * 
     * @param iconCode the icon for forecast
     * @return the weatherstate
     */
    private WeatherState parseWeatherState(String iconCode) {
        WeatherState resultState = WeatherState.UNKNOWN;
        if ("clear-day".equals(iconCode) || "clear-night".equals(iconCode)
                || "wind".equals(iconCode)) {

            resultState = WeatherState.CLEAR;
        } else if ("rain".equals(iconCode)) {
            resultState = WeatherState.RAIN;
        } else if ("cloudy".equals(iconCode)
                || "partly-cloudy-day".equals(iconCode)
                || "partly-cloudy-night".equals(iconCode)) {

            resultState = WeatherState.CLOUDS;
        } else if ("snow".equals(iconCode) || "sleet".equals(iconCode)) {

            resultState = WeatherState.SNOW;
        } else if ("fog".equals(iconCode)) {
            resultState = WeatherState.ATMOSPHERE;
        }

        /*
         * resultState = WeatherState.THUNDERSTORM; resultState =
         * WeatherState.DRIZZLE; resultState = WeatherState.EXTREME; resultState
         * = WeatherState.UNKNOWN;
         */

        return resultState;
    }

}
