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
    private static final Logger log = Logger
            .getLogger(WeatherServiceHelper.class.getName());

    protected WeatherService weatherService;
    protected ZipCodeLookup zipCodeLookup;
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
    Cache weatherDataCache;

    public WeatherServiceHelper() {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance()
                    .getCacheFactory();
            Map<Integer, Integer> props = new HashMap<Integer, Integer>();

            // Expire the cache every 6 hours.
            // TODO - this is bug it really should expire at midnight based on
            // timezone
            props.put(GCacheFactory.EXPIRATION_DELTA, 21600);
            weatherDataCache = cacheFactory.createCache(props);
        } catch (CacheException e) {
            log.log(Level.WARNING, "Failed to initialize the cache.", e);
        }

        zipCodeLookup = new ZipCodeLookup();
        // weatherService = new YahooWeatherService();
        weatherService = new ForecastIoWeatherService(zipCodeLookup);
        // weatherService = new StaticWeatherService();
    }

    @SuppressWarnings("unchecked")
    public WeatherData getWeatherData(String zipcode, TimeZone timezone) {
        WeatherDataCacheKey weatherDataCacheKey = new WeatherDataCacheKey(
                zipcode, timezone);
        WeatherData weatherData = (WeatherData) weatherDataCache
                .get(weatherDataCacheKey);
        if (weatherData == null) {
            weatherData = weatherService.getWeather(zipcode, timezone);
            if (weatherData != null) {
                LocationData locationData = zipCodeLookup
                        .getLocationDate(zipcode);
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

    public String sendMessage(String recipientName, String recipientEmail,
            String webPrefix, String zipcode, String timezoneString, String skey)
            throws MessagingException, IOException {

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

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(
                "weathernext@weathernext.appspotmail.com", "Weather.Next"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
                recipientEmail, recipientName));

        StringBuffer subject = new StringBuffer();
        subject.append(weatherData.getLocationName());
        subject.append(" ");
        subject.append(simpleDateFormat.format(weatherData.getDay()));
        subject.append(", H");
        subject.append(Math.round(weatherData.getHighTempurature()));
        subject.append(", L");
        subject.append(Math.round(weatherData.getLowTempurature()));
        msg.setSubject(subject.toString());
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

    protected String getUrlAsString(String urlString) throws IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Fetching internal URL [" + urlString + "]");
        }
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.connect();
        connection.setConnectTimeout(60000); // 60 Seconds
        connection.setReadTimeout(60000); // 60 Seconds

        BufferedInputStream reader = new BufferedInputStream(url.openStream());

        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        BufferedOutputStream cacheWriter = new BufferedOutputStream(
                byteArrayStream);

        byte[] buffer = new byte[1024];
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
