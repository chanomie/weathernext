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

@Controller
@RequestMapping(value="/weather")
public class WeatherController {
	private static final Logger log = Logger.getLogger(WeatherController.class.getName());

	@Autowired
	private WeatherServiceHelper weatherServiceHelper;

	@Autowired
	private WeatherEmailScheduleHelper weatherEmailScheduleHelper;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			@RequestParam(value="zip", required=false) String zipcode,
			@RequestParam(value="timezone", required=false) String timezoneString,
			@RequestParam(value="skey", required=false) String skey,
			HttpServletRequest request, 
			Model model) {

		TimeZone timezone = TimeZone.getTimeZone("America/Los_Angeles");
		if(timezoneString != null) {
			timezone = TimeZone.getTimeZone(timezoneString);
		}
		WeatherData weatherData = weatherServiceHelper.getWeatherData(zipcode, timezone);
		String prefix = getRootUrl(request);

		model.addAttribute("weatherData", weatherData);
		model.addAttribute("timezone", timezone);
		model.addAttribute("prefix", prefix);
		model.addAttribute("skey", skey);
		
		if(weatherData != null) {
			return "weather/weather";
		} else {
			return "weather/error";
		}
   }

	@RequestMapping(value="/text", method = RequestMethod.GET)
	public String getText(
			@RequestParam(value="zip", required=false) String zipcode,
			@RequestParam(value="timezone", required=false) String timezoneString,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		TimeZone timezone = TimeZone.getTimeZone("America/Los_Angeles");
		if(timezoneString != null) {
			timezone = TimeZone.getTimeZone(timezoneString);
		}
		WeatherData weatherData = weatherServiceHelper.getWeatherData(zipcode, timezone);
		response.setContentType("text/plain");
		model.addAttribute("weatherData", weatherData);
		model.addAttribute("timezone", timezone);
		
		return "weather/text";
   }
	
	@RequestMapping(value="/unsubscribe/{scheduleKey}", method = RequestMethod.GET)
	public String unsubscribe(
			   @PathVariable String scheduleKey,
			   Model model) {
		
		WeatherEmailSchedule weatherEmailSchedule =
				weatherEmailScheduleHelper.deleteWeatherEmailSchedule(scheduleKey);
		
		model.addAttribute("weatherEmailSchedule", weatherEmailSchedule);
		return("weather/unsubscribed");
	}

	@RequestMapping(value="/email", method = RequestMethod.GET)
	public String email(
			@RequestParam(value="zip", required=false) String zipcode,
			@RequestParam(value="timezone", required=false) String timezoneString,
			@RequestParam(value="skey", required=false) String skey,
			HttpServletRequest request,
			HttpServletResponse response,
			Principal principal) throws IOException {
		   
		String result = "weather/error";
		UserService userService = UserServiceFactory.getUserService();
		
		if(principal == null) {
			String thisURL = request.getRequestURI();
			response.sendRedirect(userService.createLoginURL(thisURL));
		} else {
			String htmlString;
			try {
				String prefix = getRootUrl(request);

				htmlString = weatherServiceHelper.sendMessage(
						userService.getCurrentUser().getNickname(), 
						userService.getCurrentUser().getEmail(),
						prefix,
						zipcode,
						timezoneString,
						skey);
				
				
				result = null;
				response.setContentType("text/html");
				BufferedInputStream reader = new BufferedInputStream(
						new ByteArrayInputStream(htmlString.getBytes()));
				BufferedOutputStream writer = new BufferedOutputStream(
						response.getOutputStream());
								
				byte[] buffer = new byte[1024];
				int len;
				while ((len = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, len);
				}
				writer.flush();
				writer.close();
												
			} catch (IOException|MessagingException e) {
	    		log.log(Level.SEVERE, "Unable to query the internal URL", e);   
			}	   
		}
		
		return result;
	}
	
	@RequestMapping(value="/scheduled", method = RequestMethod.GET)
	public String scheduledemail(
			HttpServletRequest request,
			HttpServletResponse response,
			Principal principal) throws IOException {
		   
		String result = "weather/error";
		
		try {
			String prefix = getRootUrl(request);

			List<WeatherEmailSchedule> weatherEmailScheduleList = 
					weatherEmailScheduleHelper.getReadyToSend();
			
			for(WeatherEmailSchedule weatherEmailSchedule : weatherEmailScheduleList) {
				weatherServiceHelper.sendMessage(
						weatherEmailSchedule.getRecipientName(), 
						weatherEmailSchedule.getRecipientEmail(), 
						prefix, 
						weatherEmailSchedule.getZipcode(), 
						weatherEmailSchedule.getTimezone().getID(),
						String.valueOf(weatherEmailSchedule.getKey()));
				
				weatherEmailSchedule.sendNow();
				weatherEmailScheduleHelper.putWeatherEmailSchedule(weatherEmailSchedule);
			}
						
			result = null;
											
		} catch (IOException|MessagingException e) {
    		log.log(Level.SEVERE, "Unable to query the internal URL", e);   
		}
		
		return result;
	}
	
	public static String getRootUrl(HttpServletRequest request) {
		String prefix = null;
		   
		StringBuffer requestURL = request.getRequestURL();
		Pattern p = Pattern.compile("^(https?://[^/]*)/.*$");
		Matcher m = p.matcher(requestURL);
		if(m.matches()) {
			prefix = m.group(1);
		}

		if(log.isLoggable(Level.FINE)) {
			log.fine("Extracing the request prefix from [" + requestURL + "] returns [" + prefix + "]");
		}

		return prefix;
	}
}