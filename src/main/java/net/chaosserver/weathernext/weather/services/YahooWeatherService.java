package net.chaosserver.weathernext.weather.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.chaosserver.weathernext.weather.WeatherData;
import net.chaosserver.weathernext.weather.WeatherService;
import net.chaosserver.weathernext.weather.WeatherState;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Yahoo Weather Services provides weather information from the Yahoo RSS Feed.
 * 
 * @author jreed
 */
public class YahooWeatherService implements WeatherService {
	/** The Yahoo API key loaded from the context. */
	protected static String apiKey = System.getenv("yahooweatherkey");
	
	/** Simple Date Format. */
	protected SimpleDateFormat yahooDateFormat = new SimpleDateFormat("d MMM yyyy");
	
	/** Document builder used for pasing the RSS XML from Yahoo. */
	protected DocumentBuilder dBuilder;
	
	/** Xpath processor is used to read out from the RSS feed using xpath. */
	protected XPath xpath;
	
	/** Logger. */
	private final static Logger logger = Logger.getLogger(YahooWeatherService.class.getName()); 

	/**
	 * Constructor initializes the Document Builder and XPath engine.
	 */
	public YahooWeatherService() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			xpath = XPathFactory.newInstance().newXPath();
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Unable to create XML parser", e);
			throw new IllegalStateException("Unable to create XML parser", e);
		}

		if(apiKey == null) {
			apiKey = System.getProperty("yahooweatherkey");
			if(apiKey == null) {
				throw new IllegalStateException("Failed to get yahooweatherkey from environment");
			}
		}
	}
	
	@Override
	public WeatherData getWeather(String zipcode, TimeZone timeZone) {
		WeatherData result = null;
		String woeId = getWoeId(zipcode);
		// String woeId = "12797162";
		
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date todayEnd = calendar.getTime();
		
		

		if(woeId != null) {
			try {
				String yahooURLString = "http://weather.yahooapis.com/forecastrss?w="
						+ woeId;
				URL yahooURL = new URL(yahooURLString);
				Document doc = dBuilder.parse(new BufferedInputStream(yahooURL.openStream()));
				
				double forecastNodeCount = (double) xpath.evaluate("count(//rss/channel/item/forecast)", doc, XPathConstants.NUMBER);
				for (int i = 1; i <= forecastNodeCount; i++) {
					try {
						// <yweather:forecast day="Fri" date="27 Dec 2013" low="38" high="62" text="Sunny" code="32" />
						String dateString = (String) xpath.evaluate("//rss/channel/item/forecast["+i+"]/@date", doc, XPathConstants.STRING);
						double low = (double) xpath.evaluate("//rss/channel/item/forecast["+i+"]/@low", doc, XPathConstants.NUMBER);
						double high = (double) xpath.evaluate("//rss/channel/item/forecast["+i+"]/@high", doc, XPathConstants.NUMBER);
						String description = (String) xpath.evaluate("//rss/channel/item/forecast["+i+"]/@text", doc, XPathConstants.STRING);
						double yahooCode = (double) xpath.evaluate("//rss/channel/item/forecast["+i+"]/@code", doc, XPathConstants.NUMBER);

						Date date = yahooDateFormat.parse(dateString);
						WeatherState weatherState = parseWeatherState(yahooCode);
						
						if(date.after(todayEnd)) {
							WeatherData weatherData = new WeatherData(
										date, 
										null, 
										weatherState,
										description,
										(float)high,
										(float)low,
										null,
										null);
							
							if(logger.isLoggable(Level.FINEST)) {
								logger.finest("Parsed yahooCodeInt [" + yahooCode + "] include resultstate [" + weatherState + "]"
										+ " and added : " + weatherData);
							}

							
							if(result == null) {
								result = weatherData;
							} else {
								result.addForecast(weatherData);
							}
						}
						
					} catch (ParseException e) {
						logger.log(Level.WARNING, "Failed to parse date", e);						
					}
					
				}
			} catch (SAXException|IOException|XPathExpressionException e) {
				logger.log(Level.SEVERE, "Cannot get location", e);
			}

		}
		
		return result;
	}
	
	private WeatherState parseWeatherState(double yahooCode) {
		int yahooCodeInt = (int) yahooCode;
		WeatherState resultState = WeatherState.UNKNOWN;
		// http://developer.yahoo.com/weather/#codes
		switch (yahooCodeInt) {
			case 25: // cold
			case 31: // clear (night)
			case 32: // sunny
			case 33: // fair (night)
			case 34: // fair (day)
			case 36: // hot
				resultState = WeatherState.CLEAR;
				break;
			case 26: // cloudy
			case 27: // mostly cloudy (night)
			case 28: // mostly cloudy (day)
			case 29: // partly cloudy (night)
			case 30: // partly cloudy (day)				
			case 44: // partly cloudy				
				resultState =WeatherState. CLOUDS;
				break;
			case 6: // mixed rain and sleet
			case 10: // freezing rain
			case 11: // showers
			case 12: // showers
			case 35: // mixed rain and hail
			case 40: // scattered showers
				resultState = WeatherState.RAIN;
				break;
			case 3: // severe thunderstorms
			case 4: // thunderstorms
			case 45: // thundershowers
			case 47: // isolated thundershowers
			case 37: // isolated thunderstorms
			case 38: // scattered thunderstorms
			case 39: // scattered thunderstorms
				resultState = WeatherState.THUNDERSTORM;
				break;
			case 8: // freezing drizzle
			case 9: // drizzle
				resultState = WeatherState.DRIZZLE;
				break;
			case 5: // mixed rain and snow
			case 7: // mixed snow and sleet
			case 13: // snow flurries
			case 14: // light snow showers
			case 15: // blowing snow
			case 16: // snow
			case 17: // hail
			case 18: // sleet
			case 46: //	snow showers
			case 41: // heavy snow
			case 42: // scattered snow showers
			case 43: // heavy snow
				resultState = WeatherState.SNOW;
				break;
			case 19: // dust
			case 20: // foggy
			case 21: // haze
			case 22: // smoky
			case 23: // blustery
			case 24: // windy
				resultState = WeatherState.ATMOSPHERE;
				break;
			case 0: // tornado
			case 1: // tropical storm
			case 2: // hurricane
				resultState = WeatherState.EXTREME;
				break;
			case 3200: // not available
			default:
				logger.log(Level.WARNING, "Got an Unknown Yahoo Weather Code: " + yahooCodeInt);
				resultState = WeatherState.UNKNOWN;
				break;
		}
				
		return resultState;
	}

	protected String getWoeId(String zipcode) {
		String woeId = null;

		String yahooURLString = "http://where.yahooapis.com/v1/places.q('"
				+ zipcode
				+ "')?appid="
				+ apiKey;
		
		
		try {
			URL yahooURL = new URL(yahooURLString);
			Document doc = dBuilder.parse(new BufferedInputStream(yahooURL.openStream()));			
			woeId = (String) xpath.evaluate("//places/place[1]/woeid/text()", doc, XPathConstants.STRING);
			
		} catch (SAXException|IOException|XPathExpressionException e) {
			logger.log(Level.SEVERE, "Cannot get Yahoo Woe ID", e);
		}
		
		return woeId;
	}

}

/*
curl "http://weather.yahooapis.com/forecastrss?w=2375177"
<?xml version="1.0" encoding="UTF-8"?>
<rss xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#" xmlns:yweather="http://xml.weather.yahoo.com/ns/rss/1.0" version="2.0">
<channel>
   <title>Yahoo! Weather - Carmichael, CA</title>
   <link>http://us.rd.yahoo.com/dailynews/rss/weather/Carmichael__CA/*http://weather.yahoo.com/forecast/USCA0185_f.html</link>
   <description>Yahoo! Weather for Carmichael, CA</description>
   <language>en-us</language>
   <lastBuildDate>Wed, 25 Dec 2013 5:15 pm PST</lastBuildDate>
   <ttl>60</ttl>
   <yweather:location city="Carmichael" region="CA" country="United States" />
   <yweather:units temperature="F" distance="mi" pressure="in" speed="mph" />
   <yweather:wind chill="54" direction="0" speed="0" />
   <yweather:atmosphere humidity="54" visibility="9" pressure="30.24" rising="1" />
   <yweather:astronomy sunrise="7:20 am" sunset="4:49 pm" />
   <image>
      <title>Yahoo! Weather</title>
      <width>142</width>
      <height>18</height>
      <link>http://weather.yahoo.com</link>
      <url>http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif</url>
   </image>
   <item>
      <title>Conditions for Carmichael, CA at 5:15 pm PST</title>
      <geo:lat>38.63</geo:lat>
      <geo:long>-121.33</geo:long>
      <link>http://us.rd.yahoo.com/dailynews/rss/weather/Carmichael__CA/*http://weather.yahoo.com/forecast/USCA0185_f.html</link>
      <pubDate>Wed, 25 Dec 2013 5:15 pm PST</pubDate>
      <yweather:condition text="Fair" code="33" temp="54" date="Wed, 25 Dec 2013 5:15 pm PST" />
      <description><![CDATA[<img src="http://l.yimg.com/a/i/us/we/52/33.gif"/><br />
<b>Current Conditions:</b><br />
Fair, 54 F<BR />
<BR /><b>Forecast:</b><BR />
Wed - Clear. High: 63 Low: 35<br />
Thu - Sunny. High: 66 Low: 34<br />
Fri - Sunny. High: 62 Low: 38<br />
Sat - AM Clouds/PM Sun. High: 68 Low: 36<br />
Sun - Sunny. High: 65 Low: 33<br />
<br />
<a href="http://us.rd.yahoo.com/dailynews/rss/weather/Carmichael__CA/*http://weather.yahoo.com/forecast/USCA0185_f.html">Full Forecast at Yahoo! Weather</a><BR/><BR/>
(provided by <a href="http://www.weather.com" >The Weather Channel</a>)<br/>]]></description>
      <yweather:forecast day="Wed" date="25 Dec 2013" low="35" high="63" text="Clear" code="31" />
      <yweather:forecast day="Thu" date="26 Dec 2013" low="34" high="66" text="Sunny" code="32" />
      <yweather:forecast day="Fri" date="27 Dec 2013" low="38" high="62" text="Sunny" code="32" />
      <yweather:forecast day="Sat" date="28 Dec 2013" low="36" high="68" text="AM Clouds/PM Sun" code="30" />
      <yweather:forecast day="Sun" date="29 Dec 2013" low="33" high="65" text="Sunny" code="32" />
      <guid isPermaLink="false">USCA0185_2013_12_29_7_00_PST</guid>
   </item>
</channel>
</rss>
*/
