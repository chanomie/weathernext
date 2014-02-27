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

import java.io.Serializable;
import java.util.TimeZone;

/**
 * Creates a unique key for caching a WeatherData object. WeatherData is unique
 * based on the location as well timezone, since forecast is calculated based on
 * future dates in the timezone.
 * 
 * @author jreed
 * 
 */
public class WeatherDataCacheKey implements Serializable {
    /** Unique serial number. */
    private static final long serialVersionUID = -3475102072354713156L;

    /** Zipcode. */
    protected String zipcode;

    /** Timezone. */
    protected TimeZone timezone;

    /**
     * Constructs a key object with this zipcode and timezone.
     * 
     * @param zipcode zipcode for the WeatherData object
     * @param timezone timezone of the WeatherData object
     */
    public WeatherDataCacheKey(String zipcode, TimeZone timezone) {
        this.zipcode = zipcode;
        this.timezone = timezone;
    }

    /**
     * Package private since it only needs to be accessed by other
     * WeatherDataCacheKey objects.
     * 
     * @return the zipcode
     */
    protected String getZipcode() {
        return this.zipcode;
    }

    /**
     * Package private since it only needs to be accessed by other
     * WeatherDataCacheKey objects.
     * 
     * @return the timezone
     */
    protected TimeZone getTimezone() {
        return this.timezone;
    }

    /**
     * Checks if two objects are equal by comparing the zipcode and the timezone
     * string.
     * 
     * @param o object to compare against
     * @return if objects are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof WeatherDataCacheKey)) {
            throw new ClassCastException();
        }

        WeatherDataCacheKey compareO = (WeatherDataCacheKey) o;

        boolean equals = true;
        if (getZipcode() != null) {
            equals = getZipcode().equals(compareO.getZipcode());
        } else {
            equals = compareO.getZipcode() == null;
        }

        if (equals) {
            if (getTimezone() != null) {
                equals = getTimezone().equals(compareO.getTimezone());
            } else {
                equals = compareO.getTimezone() == null;
            }
        }

        return equals;
    }

    /**
     * Returns a unique HashCode for the objects - two objects with the same
     * timezone and zipcode will have the same hashcode.
     * 
     * @return hashcode
     */
    public int hashCode() {
        String hashcode = getTimezone().toString() + getZipcode();
        return hashcode.hashCode();
    }

}
