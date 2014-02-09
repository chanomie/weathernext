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
package net.chaosserver.weathernext.weather.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * Helper function the the weather email scheduler. This class provides all the
 * interaction with the DataStore backend.
 * 
 * @author jreed
 * 
 */
public class WeatherEmailScheduleHelper {
    /** The logger. */
    private static Logger log = Logger
            .getLogger(WeatherEmailScheduleHelper.class.getName());

    /** Reference to the Google AppEngine Datastore. */
    protected DatastoreService datastore;

    /**
     * Constructs the Service Helper and initialized the datastore.
     */
    public WeatherEmailScheduleHelper() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    /**
     * Gets the weather schedule email for a specific user and zipcode
     * combination. This should always be a single unique schedule or null.
     * 
     * @param ownerId the unique identifier of the owner.
     * @param recipientEmail the recipient email of the list
     * @param zipcode the zipcode of the schedule
     * @return the unique schedule or a null if there is no matching schedule
     */
    public WeatherEmailSchedule getWeatherEmailSchedule(String ownerId,
            String recipientEmail, String zipcode) {

        Entity entity = getWeatherEmailScheduleEntity(ownerId, recipientEmail,
                zipcode);
        return convertEntityToWeatherEmailSchedule(entity);
    }

    /**
     * Gets the weather schedule email for a specific user and zipcode
     * combination. This should always be a single unique schedule or null.
     * 
     * @param ownerId the unique identifier of the owner.
     * @param recipientEmail the recipient email of the list
     * @return the unique schedule or a null if there is no matching schedule
     */
    public List<WeatherEmailSchedule> getWeatherEmailSchedule(String ownerId,
            String recipientEmail) {

        List<WeatherEmailSchedule> weatherEmailScheduleList = 
                new ArrayList<WeatherEmailSchedule>();

        Filter filter = new Query.CompositeFilter(CompositeFilterOperator.OR,
                Arrays.<Filter> asList(
                        new Query.FilterPredicate("recipientEmail",
                                FilterOperator.EQUAL, recipientEmail),
                        new Query.FilterPredicate("ownerId",
                                FilterOperator.EQUAL, ownerId)));

        Query q = new Query("EmailSchedule");
        q.setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(
                FetchOptions.Builder.withDefaults());
        for (Entity entity : results) {
            weatherEmailScheduleList
                    .add(convertEntityToWeatherEmailSchedule(entity));
        }

        return weatherEmailScheduleList;
    }

    /**
     * Gets the weather schedule entity if one matches. It will check for all
     * schedules that either match the ownerId or the recipient email.
     * 
     * @param ownerId the unique identifier of the owner.
     * @param recipientEmail the recipient email of the list
     * @param zipcode the zipcode of the schedule
     * 
     * @return the matching entity or NULL of a entity cannot be found.
     */
    protected Entity getWeatherEmailScheduleEntity(String ownerId,
            String recipientEmail, String zipcode) {

        List<Filter> filterList = Arrays.<Filter> asList(
                new Query.FilterPredicate(
                        "recipientEmail",
                        FilterOperator.EQUAL,
                        recipientEmail),
                new Query.FilterPredicate("ownerId",
                        FilterOperator.EQUAL, ownerId));

        Query q = new Query("EmailSchedule");
        Filter filter = new Query.CompositeFilter(
                CompositeFilterOperator.AND,
                Arrays.<Filter> asList(
                        new Query.CompositeFilter(
                                CompositeFilterOperator.OR, filterList),
                        new Query.FilterPredicate("zipcode",
                                FilterOperator.EQUAL, zipcode)));

        q.setFilter(filter);
        Entity entity = datastore.prepare(q).asSingleEntity();

        return entity;
    }

    /**
     * Adds or updates a weather email schedule.
     * 
     * @param ownerId the user that owns the schedule
     * @param recipientName the friendly recipient name
     * @param recipientEmail the recipient email address
     * @param zipcode the zipcode of the schedule
     * @param timezone the timezone of the schedule
     * @param nextSend the next time to send the schedule
     * @return the newly created schedule object
     */
    public WeatherEmailSchedule putSchedule(String ownerId,
            String recipientName, String recipientEmail, String zipcode,
            TimeZone timezone, Date nextSend) {

        WeatherEmailSchedule weatherEmailSchedule = new WeatherEmailSchedule(
                ownerId, recipientName, recipientEmail, zipcode, timezone,
                nextSend);

        long key = putWeatherEmailSchedule(weatherEmailSchedule);
        weatherEmailSchedule.setKey(key);

        return weatherEmailSchedule;
    }

    /**
     * Adds or updates a weather email schedule.
     * 
     * @param weatherEmailSchedule the new email schedule
     * @return the newly created schedule object
     */
    public long putWeatherEmailSchedule(
            WeatherEmailSchedule weatherEmailSchedule) {
        Entity emailScheduleEntity = getWeatherEmailScheduleEntity(
                weatherEmailSchedule.getOwnerId(),
                weatherEmailSchedule.getRecipientEmail(),
                weatherEmailSchedule.getZipcode());

        if (emailScheduleEntity == null) {
            emailScheduleEntity = new Entity("EmailSchedule");
        }

        emailScheduleEntity.setProperty("ownerId",
                weatherEmailSchedule.getOwnerId());
        emailScheduleEntity.setProperty("recipientName",
                weatherEmailSchedule.getRecipientName());
        emailScheduleEntity.setProperty("recipientEmail",
                weatherEmailSchedule.getRecipientEmail());
        emailScheduleEntity.setProperty("zipcode",
                weatherEmailSchedule.getZipcode());
        emailScheduleEntity.setProperty("timezone", weatherEmailSchedule
                .getTimezone().getID());
        emailScheduleEntity.setProperty("nextSend",
                weatherEmailSchedule.getNextSend());

        datastore.put(emailScheduleEntity);

        return emailScheduleEntity.getKey().getId();
    }

    /**
     * Deletes a schedule based on the unique key.
     * 
     * @param scheduleKey the unique key
     * @return the schedule that just deleted
     */
    public WeatherEmailSchedule deleteWeatherEmailSchedule(String scheduleKey) {
        long scheduleKeyId = Long.parseLong(scheduleKey);

        Key deleteKey = KeyFactory.createKey("EmailSchedule", scheduleKeyId);
        log.fine("Attempting to delete with key [" + scheduleKeyId + "] as ["
                + deleteKey.toString() + "]");
        WeatherEmailSchedule result = null;

        try {
            Entity emailScheduleEntity = datastore.get(deleteKey);
            result = convertEntityToWeatherEmailSchedule(emailScheduleEntity);
            datastore.delete(deleteKey);
        } catch (EntityNotFoundException e) {
            log.info("Failed to find key [" + scheduleKey + "] as ["
                    + deleteKey.toString() + "]");
        }

        return result;
    }

    /**
     * Deletes a weather email and makes sure the recipient is the owner.
     * 
     * @param recipientEmail recipient email for the schedule
     * @param scheduleKey the schedule key
     * @return the schedule that just deleted
     */
    public WeatherEmailSchedule deleteWeatherEmailSchedule(
            String recipientEmail, String scheduleKey) {
        long scheduleKeyId = Long.parseLong(scheduleKey);
        Key deleteKey = KeyFactory.createKey("EmailSchedule", scheduleKeyId);

        log.fine("Attempting to delete with key [" + scheduleKeyId + "] as ["
                + deleteKey.toString() + "]");
        WeatherEmailSchedule result = null;

        Query q = new Query("EmailSchedule");
        Filter filter = new Query.CompositeFilter(CompositeFilterOperator.AND,
                Arrays.<Filter> asList(
                        new Query.FilterPredicate("recipientEmail",
                                FilterOperator.EQUAL, recipientEmail),
                        new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                                FilterOperator.EQUAL, deleteKey)));

        q.setFilter(filter);
        Entity entity = datastore.prepare(q).asSingleEntity();
        result = convertEntityToWeatherEmailSchedule(entity);
        datastore.delete(entity.getKey());

        return result;
    }

    /**
     * Assigned a schedule to a different owner.
     * 
     * @param scheduleKey the key of the schedule to be changed.
     * @param ownerId the new owner of the schedule
     * @return the schedule that was changed
     */
    public WeatherEmailSchedule assignSchedule(String scheduleKey,
            String ownerId) {

        long scheduleKeyId = Long.parseLong(scheduleKey);
        Key assignKey = KeyFactory.createKey("EmailSchedule", scheduleKeyId);
        WeatherEmailSchedule result = null;

        try {
            Entity emailScheduleEntity = datastore.get(assignKey);
            emailScheduleEntity.setProperty("ownerId", ownerId);
            result = convertEntityToWeatherEmailSchedule(emailScheduleEntity);
            datastore.put(emailScheduleEntity);

        } catch (EntityNotFoundException e) {
            log.info("Failed to find key [" + scheduleKey + "] as ["
                    + assignKey.toString() + "]");
        }

        return result;
    }

    /**
     * Deletes all the schedules for a particular recipient.
     * 
     * @param recipientEmail the recipient email
     * @return the list of schedules that were deleted
     */
    public List<WeatherEmailSchedule> deleteWeatherEmailScheduleForRecipient(
            String recipientEmail) {
        List<WeatherEmailSchedule> weatherEmailScheduleList = 
                new ArrayList<WeatherEmailSchedule>();

        Filter filter = new Query.FilterPredicate("recipientEmail",
                FilterOperator.EQUAL, recipientEmail);
        Query q = new Query("EmailSchedule");
        q.setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(
                FetchOptions.Builder.withDefaults());
        for (Entity entity : results) {
            weatherEmailScheduleList
                    .add(convertEntityToWeatherEmailSchedule(entity));
            datastore.delete(entity.getKey());
        }

        return weatherEmailScheduleList;
    }

    /**
     * Gets a list of schedules that are ready to be sent based on the current.
     * time.
     * 
     * @return the list ready to be sent.
     */
    public List<WeatherEmailSchedule> getReadyToSend() {
        return getReadyToSend(new Date());
    }

    /**
     * Gets a list of schedules that are ready to send for a particular date.
     * 
     * @param beforeDate the date to find all schedules before
     * @return the list of schedules that should be sent before the input date
     */
    public List<WeatherEmailSchedule> getReadyToSend(Date beforeDate) {
        List<WeatherEmailSchedule> weatherEmailScheduleList =
                new ArrayList<WeatherEmailSchedule>();

        Filter filter = new Query.FilterPredicate("nextSend",
                Query.FilterOperator.LESS_THAN_OR_EQUAL, beforeDate);
        Query q = new Query("EmailSchedule");
        q.setFilter(filter);

        List<Entity> results = datastore.prepare(q).asList(
                FetchOptions.Builder.withDefaults());
        for (Entity entity : results) {
            weatherEmailScheduleList
                    .add(convertEntityToWeatherEmailSchedule(entity));
        }

        return weatherEmailScheduleList;
    }

    /**
     * Converts a Google DataStore entity into a WeatherEmailSchedule.
     * 
     * @param emailScheduleEntity the entity to convert
     * @return the schedule
     */
    public static WeatherEmailSchedule convertEntityToWeatherEmailSchedule(
            Entity emailScheduleEntity) {
        String timeZoneId = (String) emailScheduleEntity
                .getProperty("timezone");
        WeatherEmailSchedule weatherEmailSchedule = new WeatherEmailSchedule(
                (String) emailScheduleEntity.getProperty("ownerId"),
                (String) emailScheduleEntity.getProperty("recipientName"),
                (String) emailScheduleEntity.getProperty("recipientEmail"),
                (String) emailScheduleEntity.getProperty("zipcode"),
                TimeZone.getTimeZone(timeZoneId),
                (Date) emailScheduleEntity.getProperty("nextSend"),
                emailScheduleEntity.getKey().getId());

        return weatherEmailSchedule;
    }
}
