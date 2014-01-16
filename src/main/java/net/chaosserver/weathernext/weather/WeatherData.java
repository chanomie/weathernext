package net.chaosserver.weathernext.weather;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class WeatherData implements Comparable<WeatherData>, Serializable {
	private static final long serialVersionUID = -6458923855841237333L;
	
	protected Date day;
	protected String locationName;
	protected WeatherState weatherState;
	protected String weatherDescription;
	protected float highTempurature;
	protected float lowTempurature;
	protected Date sunrise;
	protected Date sunset;
	protected String attributionString;
	protected String attributionUrl;
	protected SortedSet<WeatherData> forecast;
	
	public WeatherData() {
	}
	
	public WeatherData(Date day,
				String locationName,
				WeatherState weatherState,
				String weatherDescription,
				float highTempurature,
				float lowTempurature,
				Date sunrise,
				Date sunset,
				String attributionString,
				String attributionUrl) {
		
		this.day = day;
		setLocationName(locationName);
		this.weatherState = weatherState;
		this.weatherDescription = weatherDescription;
		this.highTempurature = highTempurature;
		this.lowTempurature = lowTempurature;
		this.sunrise = sunrise;
		this.sunset = sunset;
		this.attributionString = attributionString;
		this.attributionUrl = attributionUrl;
		
		forecast = new TreeSet<WeatherData>();
	}
	
	public void addForecast(WeatherData weatherData) {
		forecast.add(weatherData);
	}
	
	public Date getDay() {
		return this.day;
	}
	
	public void setLocationName(String locationName) {
		this.locationName = locationName;
		if(getForecast() != null) {
			for(WeatherData forecastDay : getForecast()) {
				forecastDay.setLocationName(locationName);
			}
		}
	}
	
	public String getLocationName() {
		return this.locationName;
	}
	
	public WeatherState getWeatherState() {
		return this.weatherState;
	}
	
	public String getWeatherDescription() {
		return this.weatherDescription;
	}
	
	public float getHighTempurature() {
		return this.highTempurature;
	}
	
	public float getLowTempurature() {
		return this.lowTempurature;
	}
	
	public Date getSunrise() {
		return this.sunrise;
	}
	
	public Date getSunset() {
		return this.sunset;
	}
	
	public String getAttributionString() {
		return this.attributionString;
	}
	
	public String getAttributionUrl() {
		return this.attributionUrl;
	}
	
	public SortedSet<WeatherData> getForecast() {
		return this.forecast;
	}

	public int compareTo(WeatherData o) {
		return this.day.compareTo(((WeatherData)o).getDay());
	}

	/*
	 * Converts the object into a debug string.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getClass().getName());
		sb.append(" [");
		sb.append("day=");
		sb.append(this.getDay());
		sb.append(", locationName=");
		sb.append(this.getLocationName());
		sb.append(", weatherState=");
		sb.append(this.getWeatherState());
		sb.append(", weatherDescription=");
		sb.append(this.getWeatherDescription());
		sb.append(", highTempurature=");
		sb.append(this.getHighTempurature());
		sb.append(", lowTempurature=");
		sb.append(this.getLowTempurature());
		sb.append(", sunrise=");
		sb.append(this.getSunrise());
		sb.append(", sunset=");
		sb.append(this.getSunset());
		sb.append(", attributionString=");
		sb.append(this.getAttributionString());
		sb.append(", attributionUrl=");
		sb.append(this.getAttributionUrl());
		sb.append(", forecase=[");
		if(this.getForecast() != null) {
			Iterator<WeatherData> weatherDataIterator = getForecast().iterator();
			while(weatherDataIterator.hasNext()) {
				WeatherData weatherData = weatherDataIterator.next();
				sb.append(weatherData.toString());
				if(weatherDataIterator.hasNext()) {
					sb.append(", ");
				}
			}
		}
		sb.append("]]");
		return sb.toString();
		
		/*
		 * 	protected Date day;
	protected String locationName;
	protected WeatherState weatherState;
	protected String weatherDescription;
	protected float highTempurature;
	protected float lowTempurature;
	protected Date sunrise;
	protected Date sunset;
	protected SortedSet<WeatherData> forecast;

		 */
	}
}

/*
//// http://api.openweathermap.org/data/2.5/forecast/daily?lat=38.62136&lon=-121.332191&cnt=10&mode=json
//// Map the codes: http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
{
"cod":"200",
"message":0.0022,
"city":{
   "id":5352771,
   "name":"Gold River",
   "coord":{
      "lon":-121.24662,
      "lat":38.626289
   },
   "country":"US",
   "population":0
},
"cnt":10,
"list":[
   {
      "dt":1388001600,
      "temp":{
         "day":291.44,
         "min":277.66,
         "max":291.44,
         "night":277.66,
         "eve":281.82,
         "morn":291.44
      },
      "pressure":948.97,
      "humidity":33,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.2,
      "deg":64,
      "clouds":0
   },
   {
      "dt":1388088000,
      "temp":{
         "day":291.27,
         "min":274.66,
         "max":291.27,
         "night":274.66,
         "eve":279.64,
         "morn":275.02
      },
      "pressure":948.08,
      "humidity":28,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":0.36,
      "deg":180,
      "clouds":0
   },
   {
      "dt":1388174400,
      "temp":{
         "day":291.41,
         "min":275.3,
         "max":291.41,
         "night":279.83,
         "eve":281.75,
         "morn":275.3
      },
      "pressure":947.64,
      "humidity":25,
      "weather":[
         {
            "id":802,
            "main":"Clouds",
            "description":"scattered clouds",
            "icon":"03d"
         }
      ],
      "speed":1.35,
      "deg":193,
      "clouds":32
   },
   {
      "dt":1388260800,
      "temp":{
         "day":293.19,
         "min":277.49,
         "max":293.19,
         "night":280.41,
         "eve":282.39,
         "morn":277.49
      },
      "pressure":944.14,
      "humidity":25,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.26,
      "deg":11,
      "clouds":0
   },
   {
      "dt":1388347200,
      "temp":{
         "day":290.29,
         "min":275.71,
         "max":290.29,
         "night":276.18,
         "eve":280,
         "morn":275.71
      },
      "pressure":944.96,
      "humidity":32,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.06,
      "deg":297,
      "clouds":0
   },
   {
      "dt":1388433600,
      "temp":{
         "day":279.09,
         "min":270.88,
         "max":284.66,
         "night":272.32,
         "eve":284.66,
         "morn":270.88
      },
      "pressure":942.71,
      "humidity":0,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.21,
      "deg":49,
      "clouds":0
   },
   {
      "dt":1388520000,
      "temp":{
         "day":279.7,
         "min":270.06,
         "max":284.23,
         "night":271.74,
         "eve":284.23,
         "morn":270.06
      },
      "pressure":944.4,
      "humidity":0,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.03,
      "deg":104,
      "clouds":10
   },
   {
      "dt":1388606400,
      "temp":{
         "day":279.89,
         "min":269.54,
         "max":284.58,
         "night":272.15,
         "eve":284.58,
         "morn":269.54
      },
      "pressure":946.13,
      "humidity":0,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.2,
      "deg":96,
      "clouds":0
   },
   {
      "dt":1388692800,
      "temp":{
         "day":279.56,
         "min":269.87,
         "max":284.91,
         "night":277.07,
         "eve":284.91,
         "morn":269.87
      },
      "pressure":947.73,
      "humidity":0,
      "weather":[
         {
            "id":500,
            "main":"Rain",
            "description":"light rain",
            "icon":"10d"
         }
      ],
      "speed":1,
      "deg":98,
      "clouds":23,
      "rain":0.51
   },
   {
      "dt":1388779200,
      "temp":{
         "day":281.24,
         "min":274.27,
         "max":285.25,
         "night":274.27,
         "eve":285.25,
         "morn":274.89
      },
      "pressure":946.44,
      "humidity":0,
      "weather":[
         {
            "id":800,
            "main":"Clear",
            "description":"sky is clear",
            "icon":"01d"
         }
      ],
      "speed":1.36,
      "deg":75,
      "clouds":36
   }
]
}
*/