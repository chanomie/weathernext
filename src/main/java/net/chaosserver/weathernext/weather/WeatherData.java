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
package net.chaosserver.weathernext.weather;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * WeatherData contains a information about the weather for a date as well as a
 * forecast for future days.
 * 
 * @author jreed
 */
public class WeatherData implements Comparable<WeatherData>, Serializable {
    /** Serial version for serialization. */
    private static final long serialVersionUID = -6458923855841237333L;

    /** The date this object represents. */
    protected Date day;

    /** The friendly name of the location. */
    protected String locationName;

    /** The machine readable weather state. */
    protected WeatherState weatherState;

    /** The machine readable phase of the moon. */
    protected MoonPhase moonPhase;

    /** The friendly description of the forecast. */
    protected String weatherDescription;

    /** The high temperature for the day. */
    protected float highTempurature;

    /** The low temperature for the day. */
    protected float lowTempurature;

    /** The time the sun will rise. */
    protected Date sunrise;

    /** The time the sun wills set. */
    protected Date sunset;

    /** Attribution string for where this weather data came from. */
    protected String attributionString;

    /** Attribution URL for where this weather data came from. */
    protected String attributionUrl;

    /** The forecast for weather on future days. */
    protected SortedSet<WeatherData> forecast;

    /**
     * Constructs a WeatherData object with all of the information populated.
     * 
     * @param day The date this object represents
     * @param locationName The friendly name of the location
     * @param weatherState The machine readable weather state
     * @param moonPhase the machine readable lunation of the moon
     * @param weatherDescription The friendly description of the forecast
     * @param highTempurature The high temperature for the day
     * @param lowTempurature The low temperature for the day
     * @param sunrise The time the sun will rise
     * @param sunset The time the sun wills set
     * @param attributionString Attribution string for where this weather data
     *            came from
     * @param attributionUrl Attribution URL for where this weather data came
     *            from
     */
    public WeatherData(Date day, String locationName,
            WeatherState weatherState, MoonPhase moonPhase,
            String weatherDescription, float highTempurature,
            float lowTempurature, Date sunrise, Date sunset,
            String attributionString, String attributionUrl) {

        this.day = day != null ? (Date) day.clone() : null;
        setLocationName(locationName);
        this.weatherState = weatherState;
        this.moonPhase = moonPhase;
        this.weatherDescription = weatherDescription;
        this.highTempurature = highTempurature;
        this.lowTempurature = lowTempurature;
        this.sunrise = sunrise != null ? (Date) sunrise.clone() : null;
        this.sunset = sunset != null ? (Date) sunset.clone() : null;
        this.attributionString = attributionString;
        this.attributionUrl = attributionUrl;

        forecast = new TreeSet<WeatherData>();
    }

    /**
     * Adds an additional day of forecast. The forecast is sorted list that
     * holds the forecast sorted by the date of the objects.
     * 
     * @param weatherData the new forecast day to add
     */
    public void addForecast(WeatherData weatherData) {
        forecast.add(weatherData);
    }

    /**
     * The date this object represents.
     * 
     * @return The date this object represents
     */
    public Date getDay() {
        return this.day != null ? (Date) this.day.clone() : null;
    }

    /**
     * Sets the location name of the object. This will cascade its value down
     * into all the WeatherData objects in the forecast.
     * 
     * @param locationName name of the location
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
        if (getForecast() != null) {
            for (WeatherData forecastDay : getForecast()) {
                forecastDay.setLocationName(locationName);
            }
        }
    }

    /**
     * The friendly name of the location.
     * 
     * @return The friendly name of the location
     */
    public String getLocationName() {
        return this.locationName;
    }

    /**
     * The machine readable weather state.
     * 
     * @return The machine readable weather state
     */
    public WeatherState getWeatherState() {
        return this.weatherState;
    }

    /**
     * The machine readable moon phase.
     * 
     * @return the machine readable moon phase
     */
    public MoonPhase getMoonPhase() {
        return this.moonPhase;
    }

    /**
     * The friendly description of the forecast.
     * 
     * @return The friendly description of the forecast
     */
    public String getWeatherDescription() {
        return this.weatherDescription;
    }

    /**
     * The high temperature for the day.
     * 
     * @return The high temperature for the day
     */
    public float getHighTempurature() {
        return this.highTempurature;
    }

    /**
     * The low temperature for the day.
     * 
     * @return The low temperature for the day
     */
    public float getLowTempurature() {
        return this.lowTempurature;
    }

    /**
     * The time the sun will rise.
     * 
     * @return The time the sun will rise
     */
    public Date getSunrise() {
        return this.sunrise != null ? (Date) this.sunrise.clone() : null;
    }

    /**
     * The time the sun wills set.
     * 
     * @return The time the sun wills set
     */
    public Date getSunset() {
        return this.sunset != null ? (Date) this.sunset.clone() : null;
    }

    /**
     * Attribution string for where this weather data came from.
     * 
     * @return Attribution string for where this weather data came from
     */
    public String getAttributionString() {
        return this.attributionString;
    }

    /**
     * Attribution URL for where this weather data came from.
     * 
     * @return Attribution URL for where this weather data came from
     */
    public String getAttributionUrl() {
        return this.attributionUrl;
    }

    /**
     * The forecast for upcoming days.
     * 
     * @return The forecast for upcoming days
     */
    public SortedSet<WeatherData> getForecast() {
        return this.forecast;
    }

    /**
     * Compares the date of the weather objects letting you know if you date is
     * before or after.
     * 
     * @param o the object to compare to this one
     * @return if your object is before or after
     */
    public int compareTo(WeatherData o) {
        return this.day.compareTo(((WeatherData) o).getDay());
    }

    /**
     * Compares if the objects are equal - this violates the rules as objects
     * for the same date will have 'compareTo' a 0, but might not be equal.
     * 
     * @param o the object to check for equalify
     * @return if the two objects are equal
     */
    public boolean equals(WeatherData o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.getDay(), o.getDay())
                .append(this.getLocationName(), o.getLocationName())
                .append(this.getWeatherState(), o.getWeatherState())
                .append(this.getWeatherDescription(), o.getWeatherDescription())
                .append(this.getHighTempurature(), o.getHighTempurature())
                .append(this.getLowTempurature(), o.getLowTempurature())
                .append(this.getSunrise(), o.getSunrise())
                .append(this.getSunset(), o.getSunset())
                .append(this.getAttributionString(), o.getAttributionString())
                .append(this.getAttributionUrl(), o.getAttributionUrl())
                .append(this.getForecast(), o.getForecast()).isEquals();
    }

    /**
     * Returns a hashCode for the object that works the same as equals.
     * 
     * @return the hascode
     */
    public int hashCode() {
        return new HashCodeBuilder(20140207, 1529).append(this.getDay())
                .append(this.getLocationName()).append(this.getWeatherState())
                .append(this.getWeatherDescription())
                .append(this.getHighTempurature())
                .append(this.getLowTempurature()).append(this.getSunrise())
                .append(this.getSunset()).append(this.getAttributionString())
                .append(this.getAttributionUrl()).append(this.getForecast())
                .toHashCode();
    }

    /**
     * Converts the object into a debug string.
     * 
     * @return the debug string for the object
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
        sb.append(", moonPhase=");
        sb.append(this.getMoonPhase());
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
        if (this.getForecast() != null) {
            Iterator<WeatherData> weatherDataIterator = getForecast()
                    .iterator();
            while (weatherDataIterator.hasNext()) {
                WeatherData weatherData = weatherDataIterator.next();
                sb.append(weatherData.toString());
                if (weatherDataIterator.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]]");
        return sb.toString();
    }
}