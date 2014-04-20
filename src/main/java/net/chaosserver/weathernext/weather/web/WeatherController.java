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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.weather.scheduler.WeatherEmailSchedule;
import net.chaosserver.weathernext.weather.scheduler.WeatherEmailScheduleHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Weather Controller manages all the JSP pages that are used to interact with
 * the user on the website.
 * 
 * @author jreed
 * 
 */
@Controller
@RequestMapping(value = "/weather")
public class WeatherController {
    /** The logger. */
    private static final Logger log = Logger
            .getLogger(WeatherController.class.getName());

    /** Buffer length for stream copies. */
    private static final int BUFFER_LENGTH = 1024;

    /**
     * The Weather Service Schedule Helper is an autowired singleton object that
     * holds all the weather services objects and caches used by the API
     * controller.
     */
    @Autowired
    private WeatherServiceHelper weatherServiceHelper;

    /**
     * The Email Schedule Helper is an autowired singleton object that holds all
     * the Google Storage services objects and caches used by the API
     * controller.
     */
    @Autowired
    private WeatherEmailScheduleHelper weatherEmailScheduleHelper;

    /**
     * Creates a JSP representation of the weather result.
     * 
     * @param zipcode zipcode for the weather.
     * @param timezoneString timezone of the recipient
     * @param skey the subscription key if an unsubscribe link is required
     * @param emailformat true/false to indicate if it should be formatted for
     *            email
     * @param request request object
     * @param model the MVC model
     * @return SpringMVC location of the JSP
     */
    @RequestMapping(method = RequestMethod.GET)
    public String get(
            @RequestParam(value = "zip", required = false) String zipcode,
            @RequestParam(value = "timezone", required = false) String timezoneString,
            @RequestParam(value = "skey", required = false) String skey,
            @RequestParam(value = "emailformat", required = false) String emailformat,
            HttpServletRequest request, Model model) {

        try {
            Boolean emailformatBoolean = Boolean.FALSE;
            if (emailformat != null) {
                emailformatBoolean = Boolean.valueOf(emailformat);
            }

            log.fine("/weather URL Inputs zip=[" + zipcode + "], timezone=["
                    + timezoneString + "], skey=[" + skey
                    + "], emailformat=[" + emailformat
                    + "], emailformatBoolean=[" + emailformatBoolean + "]");

            TimeZone timezone = TimeZone.getTimeZone("America/Los_Angeles");
            if (timezoneString != null) {
                timezone = TimeZone.getTimeZone(timezoneString);
            }
            WeatherData weatherData = weatherServiceHelper.getWeatherData(
                    zipcode, timezone);
            String prefix = getRootUrl(request);

            String description = weatherData.getWeatherDescription();
            log.fine("Initial weather description [" + description + "]");
            if (description != null) {
                description = description.replaceAll("[\\.!]$", "");
                log.fine("After replace weather description [" + description
                        + "]");
            }

            model.addAttribute("weatherData", weatherData);
            model.addAttribute("zipcode", zipcode);
            model.addAttribute("weatherDescription", description);
            model.addAttribute("timezone", timezone);
            model.addAttribute("prefix", prefix);
            model.addAttribute("skey", skey);
            model.addAttribute("emailformat", emailformatBoolean);

            return "weather/weather";
        } catch (Exception e) {
            return "weather/error";
        }
    }

    /**
     * Creates a text representation of the weather result.
     * 
     * @param zipcode zipcode for the weather.
     * @param timezoneString timezone of the recipient
     * @param request request object
     * @param response response object
     * @param model the MVC model
     * @return SpringMVC location of the JSP
     */
    @RequestMapping(value = "/text", method = RequestMethod.GET)
    public String getText(
            @RequestParam(value = "zip", required = false) String zipcode,
            @RequestParam(value = "timezone", required = false) String timezoneString,
            HttpServletRequest request, HttpServletResponse response,
            Model model) {

        TimeZone timezone = TimeZone.getTimeZone("America/Los_Angeles");
        if (timezoneString != null) {
            timezone = TimeZone.getTimeZone(timezoneString);
        }
        WeatherData weatherData = weatherServiceHelper.getWeatherData(
                zipcode, timezone);
        response.setContentType("text/plain");
        model.addAttribute("weatherData", weatherData);
        model.addAttribute("timezone", timezone);

        return "weather/text";
    }

    /**
     * Unsubscribe page - this will trigger the unsubscription and then return a
     * confirmation page.
     * 
     * @param scheduleKey the unsubscription key
     * @param model the MVC model
     * @return the result page
     */
    @RequestMapping(value = "/unsubscribe/{scheduleKey}", method = RequestMethod.GET)
    public String unsubscribe(@PathVariable String scheduleKey, Model model) {

        WeatherEmailSchedule weatherEmailSchedule = weatherEmailScheduleHelper
                .deleteWeatherEmailSchedule(scheduleKey);

        model.addAttribute("weatherEmailSchedule", weatherEmailSchedule);
        return ("weather/unsubscribed");
    }

    /**
     * Generates an email message to be sent given the parameters passed in.
     * 
     * @param zipcode zipcode to get weather for and send the mail.
     * @param timezoneString the string indicating the timezone of the recipient
     * @param skey the subscription key if there is one so that the user can
     *            unsubscribe
     * @param request http request
     * @param response http response
     * @param principal the recipient of the mail
     * @return SpringMVC mapping to the JSP to show the mail sent
     * @throws IOException
     */
    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public String email(
            @RequestParam(value = "zip", required = false) String zipcode,
            @RequestParam(value = "timezone", required = false) String timezoneString,
            @RequestParam(value = "skey", required = false) String skey,
            HttpServletRequest request, HttpServletResponse response,
            Principal principal) {

        if (zipcode == null || zipcode.isEmpty()) {
            throw new IllegalArgumentException("Missing zip URL parameter");
        }
        String result = "weather/error";
        String weatherStatusSendAll = null;
        UserService userService = UserServiceFactory.getUserService();

        if (principal == null) {
            String thisURL = request.getRequestURL().toString();
            try {
                response.sendRedirect(userService.createLoginURL(thisURL));
            } catch (IOException e) {
                log.log(Level.WARNING, "error sending redirect", e);
            }
        } else {
            String htmlString;
            try {
                String prefix = getRootUrl(request);

                htmlString = weatherServiceHelper.sendMessage(userService
                        .getCurrentUser().getNickname(), userService
                        .getCurrentUser().getEmail(), prefix, zipcode,
                        weatherStatusSendAll, timezoneString, skey);

                result = null;
                response.setContentType("text/html");
                BufferedInputStream reader = new BufferedInputStream(
                        new ByteArrayInputStream(htmlString.getBytes("UTF-8")));
                BufferedOutputStream writer = new BufferedOutputStream(
                        response.getOutputStream());

                byte[] buffer = new byte[BUFFER_LENGTH];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, len);
                }
                writer.flush();
                writer.close();

            } catch (IOException | MessagingException e) {
                log.log(Level.SEVERE, "Unable to query the internal URL", e);
            }
        }

        return result;
    }

    /**
     * Triggers all the scheduled email in the system. It will loop through all
     * scheduled emails that have a SendOn date before the current time. Then it
     * will send the email for it which updates the SendOn date to the current
     * time.
     * 
     * This is restricted in web.xml to only be callable by an admin user and is
     * intended to only be called by the cron service.
     * 
     * @param request the http request
     * @param principal the admin user
     * @return returns a SpringMVC mapping to an error page or null
     */
    @RequestMapping(value = "/scheduled", method = RequestMethod.GET)
    public String scheduledemail(HttpServletRequest request,
            Principal principal) {

        String result = "weather/error";

        try {
            String prefix = getRootUrl(request);

            List<WeatherEmailSchedule> weatherEmailScheduleList = weatherEmailScheduleHelper
                    .getReadyToSend();

            for (WeatherEmailSchedule weatherEmailSchedule : weatherEmailScheduleList) {
                weatherServiceHelper.sendMessage(
                        weatherEmailSchedule.getRecipientName(),
                        weatherEmailSchedule.getRecipientEmail(), prefix,
                        weatherEmailSchedule.getZipcode(),
                        weatherEmailSchedule.getWeatherStatus(),
                        weatherEmailSchedule.getTimezone().getID(),
                        String.valueOf(weatherEmailSchedule.getKey()));

                weatherEmailSchedule.sendNow();
                weatherEmailScheduleHelper
                        .putWeatherEmailSchedule(weatherEmailSchedule);
            }

            // result = null;

        } catch (IOException | MessagingException e) {
            log.log(Level.SEVERE, "Unable to query the internal URL", e);
        }

        return result;
    }

    /**
     * Gets the Root URL out of the request object. This is required for being
     * able to provide absolute links to the images required in the emails.
     * 
     * <ul>
     * <li>http://weathernext.appspot.com/weather ==
     * http://weathernext.appspot.com</li>
     * <li>https://weathernext.appspot.com/weather ==
     * https://weathernext.appspot.com</li>
     * <li>https://weathernext.appspot.com/weather/mail ==
     * https://weathernext.appspot.com</li>
     * </ul>
     * 
     * @param request the request to extract the prefix from
     * @return the prefix from the request object
     */
    public static String getRootUrl(HttpServletRequest request) {
        String prefix = null;

        StringBuffer requestURL = request.getRequestURL();
        Pattern p = Pattern.compile("^(https?://[^/]*)/.*$");
        Matcher m = p.matcher(requestURL);
        if (m.matches()) {
            prefix = m.group(1);
        }

        if (log.isLoggable(Level.FINE)) {
            log.fine("Extracing the request prefix from [" + requestURL
                    + "] returns [" + prefix + "]");
        }

        return prefix;
    }
}
