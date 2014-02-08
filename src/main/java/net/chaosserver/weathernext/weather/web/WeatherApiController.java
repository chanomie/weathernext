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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.chaosserver.weathernext.weather.scheduler.WeatherEmailSchedule;
import net.chaosserver.weathernext.weather.scheduler.WeatherEmailScheduleHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping(value = "/api")
public class WeatherApiController {
    private static final Logger log = Logger
            .getLogger(WeatherApiController.class.getName());

    protected UserService userService = UserServiceFactory.getUserService();

    @Autowired
    private WeatherEmailScheduleHelper weatherEmailScheduleHelper;

    /**
     * Create a warmpup API inside of the Spring Framework.
     * 
     * Google App Engine will take the app out of memory if nothing is hitting
     * it, which causes the first request to the app to take 30+ seconds. To try
     * and mitigate this problem the warmup API is called directly to by the
     * HTML on load.
     * 
     * @return
     */
    @RequestMapping(value = "/v1/warmup", produces = "text/css")
    public @ResponseBody
    String warmpup(HttpServletResponse response) {
        response.setContentType("text/css");
        response.setHeader("Cache-Control",
                "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
        return "";
    }

    /**
     * This method is used to determine the authentication status of the user.
     * It will also return the paths for the login and logout URL
     * 
     * @param request the httprequest object
     * @param principal the principal object for the J2EE context
     * @param returnPath a URL where the user should be returned after logging
     *            in or logging out. it will be appended to the login/logout URL
     * @return a JSON object providing login status and URLs for the
     *         login/logout URL
     */
    @RequestMapping(value = "/v1/google/status")
    public @ResponseBody
    Map<String, String> getGoogleStatus(
            HttpServletRequest request,
            Principal principal,
            @RequestParam(value = "returnPath", required = false) String returnPath) {

        String thisURL = returnPath != null ? returnPath : request
                .getRequestURI();

        Map<String, String> urlMap = new HashMap<String, String>();
        urlMap.put("googleLogoutUrl", userService.createLogoutURL(thisURL));
        urlMap.put("googleLoginUrl", userService.createLoginURL(thisURL));
        if (principal != null) {
            urlMap.put("googleLoginStatus", "true");
            urlMap.put("recipientEmail", userService.getCurrentUser()
                    .getEmail());
            urlMap.put("nickname", userService.getCurrentUser().getNickname());
        } else {
            urlMap.put("googleLoginStatus", "false");
        }

        return urlMap;
    }

    /**
     * Gets your Google User Id
     */
    @RequestMapping(value = "/ownerId")
    public @ResponseBody
    Map<String, String> getGoogleStatus(Principal principal) {

        if (principal == null)
            throw new SecurityException("Requires user principal");
        Map<String, String> responseJson = new HashMap<String, String>();

        responseJson.put("recipientEmail", userService.getCurrentUser()
                .getEmail());
        responseJson.put("ownerId", userService.getCurrentUser().getUserId());
        responseJson
                .put("nickname", userService.getCurrentUser().getNickname());

        return responseJson;
    }

    /**
     * Gets a list of all schedules currently stored in the system. By default
     * it only lists schedules that are going to send.
     * 
     * @param showall (default: false) should it show all schedules including
     *            ones that aren't ready
     * @return
     */
    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> getSchedules(
            @RequestParam(value = "zip", required = false) String zipcode,
            Principal principal) {

        if (principal == null)
            throw new SecurityException("Requires user principal");
        List<Map<String, String>> weatherScheduleJsonList = new ArrayList<Map<String, String>>();

        String recipientEmail = userService.getCurrentUser().getEmail();
        String ownerId = userService.getCurrentUser().getUserId();

        log.finer("Getting schedule for recipientEmail [" + recipientEmail
                + "], ownerId [" + ownerId + "]");

        if (recipientEmail != null && zipcode != null) {
            WeatherEmailSchedule weatherEmailSchedule = weatherEmailScheduleHelper
                    .getWeatherEmailSchedule(ownerId, recipientEmail, zipcode);

            weatherScheduleJsonList.add(weatherEmailSchedule.toMap());
        } else if (recipientEmail != null) {
            List<WeatherEmailSchedule> weatherScheduleList = weatherEmailScheduleHelper
                    .getWeatherEmailSchedule(ownerId, recipientEmail);

            for (WeatherEmailSchedule weatherEmailSchedule : weatherScheduleList) {
                weatherScheduleJsonList.add(weatherEmailSchedule.toMap());
            }
        }

        return weatherScheduleJsonList;
    }

    /**
     * Assigns a schedule to a specific owner
     * 
     * @param scheduleKey the schedule key to assign
     * @param ownerId the owner id to assign it to
     * @return the schedule that was deleted.
     */
    @RequestMapping(value = "/schedule/assign", method = { RequestMethod.GET })
    public @ResponseBody
    Map<String, String> assignSchedule(
            @RequestParam(value = "scheduleKey", required = false) String scheduleKey,
            @RequestParam(value = "ownerId", required = false) String ownerId,
            Principal principal) {

        if (principal == null)
            throw new SecurityException("Requires login");
        if (!userService.isUserAdmin())
            throw new SecurityException("Requires admin user");

        WeatherEmailSchedule weatherEmailSchedule = weatherEmailScheduleHelper
                .assignSchedule(scheduleKey, ownerId);

        if (weatherEmailSchedule != null) {
            return weatherEmailSchedule.toMap();
        } else {
            return null;
        }
    }

    /**
     * Deletes a schedule from the system
     * 
     * @param scheduleKey
     * @return the schedule that was deleted.
     * @deprecated for compatibility it is better to use the DELETE method
     */
    @RequestMapping(value = "/schedule/delete", method = { RequestMethod.POST })
    public @ResponseBody
    List<Map<String, String>> deleteSchedulePost(
            @RequestParam(value = "scheduleKey", required = false) String scheduleKey,
            Principal principal) {

        if (principal == null)
            throw new SecurityException("Requires login");

        List<Map<String, String>> weatherScheduleJsonList = new ArrayList<Map<String, String>>();

        if (scheduleKey != null) {
            WeatherEmailSchedule weatherEmailSchedule = weatherEmailScheduleHelper
                    .deleteWeatherEmailSchedule(scheduleKey);

            if (weatherEmailSchedule != null) {
                weatherScheduleJsonList.add(weatherEmailSchedule.toMap());
            }
        }

        return weatherScheduleJsonList;
    }

    @RequestMapping(value = "/schedule/{scheduleKey}", method = RequestMethod.DELETE)
    public @ResponseBody
    Map<String, String> deleteSchedule(@PathVariable String scheduleKey,
            Principal principal) {

        if (principal == null)
            throw new SecurityException("Requires login");
        String recipientEmail = userService.getCurrentUser().getEmail();

        WeatherEmailSchedule weatherEmailSchedule = weatherEmailScheduleHelper
                .deleteWeatherEmailSchedule(recipientEmail, scheduleKey);

        return weatherEmailSchedule.toMap();
    }

    @RequestMapping(value = "/schedule", method = { RequestMethod.POST })
    public @ResponseBody
    Map<String, String> addSchedule(
            @RequestParam(value = "zip", required = false) String zipcode,
            @RequestParam(value = "timezone", required = false) String timezoneString,
            @RequestParam(value = "sendTime", required = false) String sendTimeString,
            Principal principal) {

        if (principal == null)
            throw new SecurityException("Requires login");
        String ownerId = userService.getCurrentUser().getUserId();
        String recipientEmail = userService.getCurrentUser().getEmail();
        String recipientName = userService.getCurrentUser().getNickname();

        log.finer("Got ownerId [" + ownerId + "], recipientName ["
                + recipientName + "], recipientEmail [" + recipientEmail
                + "], zip [" + zipcode + "], timezone [" + timezoneString
                + "], sendTime [" + sendTimeString + "]");

        TimeZone timezone = TimeZone.getTimeZone(timezoneString);
        Date sendTime = new Date(Long.parseLong(sendTimeString));

        Calendar calender = Calendar.getInstance(timezone);
        if (calender.getTime().after(sendTime)) {
            Calendar newSendTime = Calendar.getInstance(timezone);
            newSendTime.setTime(sendTime);
            while (newSendTime.before(calender)) {
                newSendTime.add(Calendar.DAY_OF_YEAR, 1);
            }

            sendTime = newSendTime.getTime();
        }

        WeatherEmailSchedule weatherEmailSchedule = weatherEmailScheduleHelper
                .putSchedule(ownerId, recipientName, recipientEmail, zipcode,
                        timezone, sendTime);

        return weatherEmailSchedule.toMap();
    }

}
