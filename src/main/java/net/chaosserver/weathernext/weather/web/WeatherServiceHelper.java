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
package net.chaosserver.weathernext.weather.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.weather.WeatherService;
import net.chaosserver.weathernext.weather.services.ForecastIoWeatherService;
import net.chaosserver.weathernext.weather.services.YahooWeatherService;
import net.chaosserver.weathernext.zipcode.LocationData;
import net.chaosserver.weathernext.zipcode.ZipCodeLookup;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

/**
 * Weather Service Helper interfaces with the weather services and geo services.
 * It is designed to be used as a singleton bean and includes internal caching
 * of the values from the external calls.
 * 
 * @author jreed
 */
public class WeatherServiceHelper {
    /** Java logger. */
    private static final Logger log = Logger
            .getLogger(WeatherServiceHelper.class.getName());

    /** Buffer size when connecting streams. */
    private static final int BUFFER_SIZE = 1024;

    /** The date format used in the message subject. */
    private static final SimpleDateFormat SUBJECT_DATE_FORMAT = new SimpleDateFormat(
            "MM/dd");

    /** Connection timeout set to 60 seconds; max allowed by Google App Engine. */
    private static final int CONNECTION_TIMEOUT = 60000;

    /** Cache Expires every 6 hours. */
    private static final int CACHE_EXPIRE = 21600;

    /** Primary WeatherService to be used. */
    protected WeatherService weatherService;

    /** WeatherService to use when the primary throws an exception. */
    protected WeatherService fallbackService;

    /** Zipcode Lookup object. */
    protected ZipCodeLookup zipCodeLookup;

    /** The JavaMail sessions. */
    protected Session mailSession = Session.getDefaultInstance(
            new Properties(), null);

    /** Holds a cache of weather data objects. */
    protected Cache weatherDataCache;

    /**
     * Generates a new WeatherService Helper. This contains internally cache
     * objects and is meant to be autowired as a Singleton to make sure there is
     * only one instance of the cache.
     */
    public WeatherServiceHelper() {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance()
                    .getCacheFactory();
            Map<Integer, Integer> cacheProperties = new HashMap<Integer, Integer>();

            // Expire the cache every 6 hours.
            // TODO - this is bug it really should expire at midnight based on
            // timezone
            cacheProperties.put(GCacheFactory.EXPIRATION_DELTA, CACHE_EXPIRE);
            weatherDataCache = cacheFactory.createCache(cacheProperties);
        } catch (CacheException e) {
            log.log(Level.WARNING, "Failed to initialize the cache.", e);
        }

        zipCodeLookup = new ZipCodeLookup();
        weatherService = new ForecastIoWeatherService(zipCodeLookup);
        fallbackService = new YahooWeatherService();
        // weatherService = new StaticWeatherService();
    }

    /**
     * Gets the WeatherData for a particular zipcode and time format. It will
     * use the primary service and then fallback to the secondary.
     * 
     * @param zipcode zipcode for the weather
     * @param timezone the zone used to calculate tomorrow
     * @return the weather and forecast
     */
    @SuppressWarnings("unchecked")
    public WeatherData getWeatherData(String zipcode, TimeZone timezone) {
        WeatherDataCacheKey weatherDataCacheKey = new WeatherDataCacheKey(
                zipcode, timezone);
        WeatherData weatherData = (WeatherData) weatherDataCache
                .get(weatherDataCacheKey);
        if (weatherData == null) {
            try {
                weatherData = weatherService.getWeather(zipcode, timezone);
            } catch (Exception e) {
                log.log(Level.WARNING,
                        "Error getting from primary weather source", e);
                weatherData = fallbackService.getWeather(zipcode, timezone);
            }
            if (weatherData != null) {
                LocationData locationData = zipCodeLookup
                        .getLocationData(zipcode);
                if (locationData != null && locationData.getCity() != null) {
                    weatherData.setLocationName(locationData.getCity());
                } else {
                    weatherData = null;
                }
            }
            weatherDataCache.put(weatherDataCacheKey, weatherData);
        }

        return weatherData;
    }

    /**
     * Sends an email message using the Mail Services to the recipient.
     * 
     * @param recipientName the friendly name of the person receiving the mail
     * @param recipientEmail the email address to send to
     * @param webPrefix prefix of where the site is being hosted at to allow the
     *            call to make requests of the JSPs for HTML/Text content of the
     *            mail.
     * @param zipcode zipcode of the weather forecast to pull
     * @param timezoneString the timezone to use when calculating "tomorrow"
     * @param skey the subscription key if available
     * @return the HTML version of the mail that was sent
     * @throws MessagingException exception sending the message
     * @throws IOException exception grabbing the HTML/Text versions
     */
    public String sendMessage(String recipientName, String recipientEmail,
            String webPrefix, String zipcode, String timezoneString,
            String skey) throws MessagingException, IOException {

        TimeZone timezone = TimeZone.getTimeZone("America/Los_Angeles");
        if (timezoneString != null) {
            timezone = TimeZone.getTimeZone(timezoneString);
        }

        StringBuffer parameters = new StringBuffer();
        parameters.append("zip=");
        parameters.append(URLEncoder.encode(zipcode, "UTF-8"));
        parameters.append("&emailformat=true");
        if (timezoneString != null) {
            parameters.append("&timezone=");
            parameters.append(URLEncoder.encode(timezoneString, "UTF-8"));
        }
        if (skey != null) {
            parameters.append("&skey=");
            parameters.append(URLEncoder.encode(skey, "UTF-8"));
        }

        WeatherData weatherData = getWeatherData(zipcode, timezone);
        String htmlString = getUrlAsString(webPrefix + "/weather?"
                + parameters.toString());
        String textString = getUrlAsString(webPrefix + "/weather/text?"
                + parameters.toString());

        StringBuffer subject = new StringBuffer();
        String weatherStateIcon = "\uD83D\uDCA3";
        switch (weatherData.getWeatherState()) {
            case CLEAR:
                weatherStateIcon = "\u2600"; // 9728
                break;
            case CLOUDS:
                weatherStateIcon = "\u2601"; // 9729
                break;
            case RAIN:
                weatherStateIcon = "\u2614"; // 9748
                break;
            case THUNDERSTORM:
                weatherStateIcon = "\u26A1"; // 9889
                break;
            case DRIZZLE:
                weatherStateIcon = "\u2602"; // 9730
                break;
            case SNOW:
                weatherStateIcon = "\u2603"; // 9731
                break;
            case ATMOSPHERE:
                weatherStateIcon = "\u2668"; // 9832
                break;
            case EXTREME:
                weatherStateIcon = "\uD83C\uDF0B"; // 127755
                break;
            default:
                weatherStateIcon = "\uD83D\uDCA3"; // 128163
                break;
        }
        String moonIcon = null;
        if (weatherData.getMoonPhase() != null) {
            switch (weatherData.getMoonPhase()) {
                case FULL:
                    moonIcon = "\uD83C\uDF15";
                    break;
                case WAXING_GIBBOUS:
                    moonIcon = "\uD83C\uDF14";
                    break;
                case FIRST_QUARTER:
                    moonIcon = "\uD83C\uDF13";
                    break;
                case WAXING_CRESCENT:
                    moonIcon = "\uD83C\uDF12";
                    break;
                case NEW:
                    moonIcon = "\uD83C\uDF11";
                    break;
                case WANING_CRESCENT:
                    moonIcon = "\uD83C\uDF18";
                    break;
                case THIRD_QUARTER:
                    moonIcon = "\uD83C\uDF17";
                    break;
                case WANING_GIBBOUS:
                    moonIcon = "\uD83C\uDF16";
                    break;
                default:
                    moonIcon = null;
                    break;
            }
        }

        MimeMessage msg = new MimeMessage(mailSession);

        if (moonIcon == null) {
            msg.setFrom(new InternetAddress(
                    "weathernext@weathernext.appspotmail.com",
                    weatherStateIcon + " " + "Weather.Next", "UTF-8"));
        } else {
            msg.setFrom(new InternetAddress(
                    "weathernext@weathernext.appspotmail.com",
                    weatherStateIcon + " " + moonIcon + " " + "Weather.Next",
                    "UTF-8"));

            msg.setSubject(
                    weatherStateIcon + " " + moonIcon + " "
                            + subject.toString(), "UTF-8");
        }

        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
                recipientEmail, recipientName));

        subject.append(weatherData.getLocationName());
        subject.append(" ");
        subject.append(SUBJECT_DATE_FORMAT.format(weatherData.getDay()));
        subject.append(", H");
        subject.append(Math.round(weatherData.getHighTempurature()));
        subject.append(", L");
        subject.append(Math.round(weatherData.getLowTempurature()));
        msg.setSubject(subject.toString());

        /*
         * Emoji was making the subject too long, moving into the From field.
         * @formatter:off if (moonIcon == null) {
         * msg.setSubject(weatherStateIcon + " " + subject.toString(), "UTF-8");
         * } else { msg.setSubject( weatherStateIcon + " " + moonIcon + " " +
         * subject.toString(), "UTF-8"); }
         * @formatter:on
         */
        Multipart mp = new MimeMultipart();

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(textString, "text/plain");
        mp.addBodyPart(bodyPart);

        bodyPart = new MimeBodyPart();
        bodyPart.setContent(htmlString, "text/html");
        mp.addBodyPart(bodyPart);
        msg.setContent(mp);
        Transport.send(msg);

        return htmlString;
    }

    /**
     * Requests a URL and then returns the content of the response as a string.
     * Used internally to grab the HTML and Text versions as a String.
     * 
     * @param urlString the string to grab
     * @return the result as string
     * @throws IOException error grabbing the result.
     */
    protected String getUrlAsString(String urlString) throws IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Fetching internal URL [" + urlString + "]");
        }
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.connect();
        connection.setConnectTimeout(CONNECTION_TIMEOUT); // 60 Seconds
        connection.setReadTimeout(CONNECTION_TIMEOUT); // 60 Seconds

        BufferedInputStream reader = new BufferedInputStream(url.openStream());

        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        BufferedOutputStream cacheWriter = new BufferedOutputStream(
                byteArrayStream);

        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = reader.read(buffer)) != -1) {
            cacheWriter.write(buffer, 0, len);
        }
        cacheWriter.flush();
        cacheWriter.close();
        reader.close();

        String htmlString = byteArrayStream.toString("UTF-8");
        return htmlString;
    }
}
