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

public class WeatherEmailScheduleHelper {
	private static final Logger log = Logger.getLogger(WeatherEmailScheduleHelper.class.getName());
	
	DatastoreService datastore;
	public WeatherEmailScheduleHelper() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	/**
	 * Gets the weather schedule email for a specific user and zipcode combination.  This
	 * should always be a single unique schedule or null.
	 * 
	 * @param ownerId the unique identifier of the owner.
	 * @param recipientEmail the recipient email of the list
	 * @param zipcode the zipcode of the schedule
	 * @return the unique schedule or a null if there is no matching schedule
	 */
	public WeatherEmailSchedule getWeatherEmailSchedule (
			String ownerId,
			String recipientEmail,
			String zipcode
			) {
		
		Entity entity = getWeatherEmailScheduleEntity(ownerId, recipientEmail, zipcode);
		return convertEntityToWeatherEmailSchedule(entity);
	}
	
	/**
	 * Gets the weather schedule email for a specific user and zipcode combination. This
	 * should always be a single unique schedule or null.
	 * 
	 * @param ownerId the unique identifier of the owner.
	 * @param recipientEmail the recipient email of the list
	 * @return the unique schedule or a null if there is no matching schedule
	 */
	public List<WeatherEmailSchedule> getWeatherEmailSchedule(
			String ownerId,
			String recipientEmail) {

		List<WeatherEmailSchedule> weatherEmailScheduleList = new ArrayList<WeatherEmailSchedule>();
		
		Filter filter = new	Query.CompositeFilter(CompositeFilterOperator.OR, Arrays.<Filter>asList(
							new Query.FilterPredicate("recipientEmail", FilterOperator.EQUAL, recipientEmail),
							new Query.FilterPredicate("ownerId", FilterOperator.EQUAL, ownerId)));
		
		Query q = new Query("EmailSchedule");
		q.setFilter(filter);

		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : results) {
			weatherEmailScheduleList.add(convertEntityToWeatherEmailSchedule(entity));
		}
		
		return weatherEmailScheduleList;
	}
	
	/**
	 * Gets the weather schedule entity if one matches.  It will check for all schedules that
	 * either match the ownerId or the recipient email.
	 * 
	 * @param ownerId the unique identifier of the owner.
	 * @param recipientEmail the recipient email of the list
	 * @param zipcode the zipcode of the schedule
	 * 
	 * @throws PreparedQuery.TooManyResultsException if more than one result is returned - 
	 *         which should not be allowed.
	 * @return the matching entity or NULL of a entity cannot be found.
	 */
	protected Entity getWeatherEmailScheduleEntity (
			String ownerId,
			String recipientEmail,
			String zipcode
			) {

		Query q = new Query("EmailSchedule");
		Filter filter = 
			new Query.CompositeFilter(CompositeFilterOperator.AND, Arrays.<Filter>asList(
				new	Query.CompositeFilter(CompositeFilterOperator.OR, Arrays.<Filter>asList(
						new Query.FilterPredicate("recipientEmail", FilterOperator.EQUAL, recipientEmail),
						new Query.FilterPredicate("ownerId", FilterOperator.EQUAL, ownerId))),
				new Query.FilterPredicate("zipcode", FilterOperator.EQUAL, zipcode)));
		
		q.setFilter(filter);
		Entity entity = datastore.prepare(q).asSingleEntity();

		return entity;
	}
	
	public WeatherEmailSchedule putSchedule(
			String ownerId,
			String recipientName,
			String recipientEmail,
			String zipcode,
			TimeZone timezone,
			Date nextSend) {		
		
		WeatherEmailSchedule weatherEmailSchedule = new WeatherEmailSchedule(
				ownerId, recipientName, recipientEmail, zipcode, timezone, nextSend);
		
		long key = putWeatherEmailSchedule(weatherEmailSchedule);
		weatherEmailSchedule.setKey(key);
		
		return weatherEmailSchedule;
	}
	
	public long putWeatherEmailSchedule(WeatherEmailSchedule weatherEmailSchedule) {
		Entity emailScheduleEntity = getWeatherEmailScheduleEntity(
				weatherEmailSchedule.getOwnerId(),
				weatherEmailSchedule.getRecipientEmail(),
				weatherEmailSchedule.getZipcode()
				);
		
		if(emailScheduleEntity == null) {
			emailScheduleEntity = new Entity("EmailSchedule");
		}
		
		emailScheduleEntity.setProperty("ownerId", weatherEmailSchedule.getOwnerId());
		emailScheduleEntity.setProperty("recipientName", weatherEmailSchedule.getRecipientName());
		emailScheduleEntity.setProperty("recipientEmail", weatherEmailSchedule.getRecipientEmail());
		emailScheduleEntity.setProperty("zipcode", weatherEmailSchedule.getZipcode());
		emailScheduleEntity.setProperty("timezone", weatherEmailSchedule.getTimezone().getID());
		emailScheduleEntity.setProperty("nextSend", weatherEmailSchedule.getNextSend());
		
		datastore.put(emailScheduleEntity);
		
		return emailScheduleEntity.getKey().getId();
	}
		
	public WeatherEmailSchedule deleteWeatherEmailSchedule(String scheduleKey) {
		long scheduleKeyId = Long.parseLong(scheduleKey);
		
		Key deleteKey = KeyFactory.createKey("EmailSchedule", scheduleKeyId);
		log.fine("Attempting to delete with key [" + scheduleKeyId + "] as [" + deleteKey.toString() + "]");
		WeatherEmailSchedule result = null;
		
		try {
			Entity emailScheduleEntity = datastore.get(deleteKey);
			result = convertEntityToWeatherEmailSchedule(emailScheduleEntity);
			datastore.delete(deleteKey);
		} catch (EntityNotFoundException e) {
			log.info("Failed to find key [" + scheduleKey + "] as [" + deleteKey.toString() + "]");
		}
		
		return result;
	}

	public WeatherEmailSchedule deleteWeatherEmailSchedule(String recipientEmail, String scheduleKey) {
		long scheduleKeyId = Long.parseLong(scheduleKey);
		Key deleteKey = KeyFactory.createKey("EmailSchedule", scheduleKeyId);
		
		log.fine("Attempting to delete with key [" + scheduleKeyId + "] as [" + deleteKey.toString() + "]");
		WeatherEmailSchedule result = null;
		
		Query q = new Query("EmailSchedule");
		Filter filter = new Query.CompositeFilter(CompositeFilterOperator.AND, Arrays.<Filter>asList(
				new Query.FilterPredicate("recipientEmail", FilterOperator.EQUAL, recipientEmail),
				new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, deleteKey)));
		
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
			log.info("Failed to find key [" + scheduleKey + "] as [" + assignKey.toString() + "]");
		}
		
		return result;
	}


	public List<WeatherEmailSchedule> deleteWeatherEmailScheduleForRecipient(String recipientEmail) {
		List<WeatherEmailSchedule> weatherEmailScheduleList = new ArrayList<WeatherEmailSchedule>();

		Filter filter = new Query.FilterPredicate("recipientEmail", FilterOperator.EQUAL, recipientEmail);
		Query q = new Query("EmailSchedule");
		q.setFilter(filter);

		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : results) {
			weatherEmailScheduleList.add(convertEntityToWeatherEmailSchedule(entity));
			datastore.delete(entity.getKey());
		}
		
		return weatherEmailScheduleList;
	}
	
	public List<WeatherEmailSchedule> getReadyToSend() {
		return getReadyToSend(new Date());
	}
	
	public List<WeatherEmailSchedule> getAll() {
		List<WeatherEmailSchedule> weatherEmailScheduleList = new ArrayList<WeatherEmailSchedule>();
		Query q = new Query("EmailSchedule");
		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : results) {
			weatherEmailScheduleList.add(convertEntityToWeatherEmailSchedule(entity));
		}
		
		return weatherEmailScheduleList;
	}

	public List<WeatherEmailSchedule> getReadyToSend(Date beforeDate) {
		List<WeatherEmailSchedule> weatherEmailScheduleList = new ArrayList<WeatherEmailSchedule>();
		
		Filter filter = new Query.FilterPredicate("nextSend", Query.FilterOperator.LESS_THAN_OR_EQUAL,beforeDate);
		Query q = new Query("EmailSchedule");
		q.setFilter(filter);
				
		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for(Entity entity : results) {
			weatherEmailScheduleList.add(convertEntityToWeatherEmailSchedule(entity));
		}
		
		return weatherEmailScheduleList;
	}

	public WeatherEmailSchedule convertEntityToWeatherEmailSchedule(Entity emailScheduleEntity) {
		String timeZoneId = (String) emailScheduleEntity.getProperty("timezone");
		WeatherEmailSchedule weatherEmailSchedule = new WeatherEmailSchedule(
				(String)emailScheduleEntity.getProperty("ownerId"),
				(String)emailScheduleEntity.getProperty("recipientName"),
				(String)emailScheduleEntity.getProperty("recipientEmail"),
				(String)emailScheduleEntity.getProperty("zipcode"),
				TimeZone.getTimeZone(timeZoneId),
				(Date)emailScheduleEntity.getProperty("nextSend"),
				emailScheduleEntity.getKey().getId()
				);
		
		return weatherEmailSchedule;
	}
}
