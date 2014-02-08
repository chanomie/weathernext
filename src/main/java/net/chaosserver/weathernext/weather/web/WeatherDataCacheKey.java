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

public class WeatherDataCacheKey implements Serializable {
    private static final long serialVersionUID = -3475102072354713156L;
    String zipcode;
    TimeZone timezone;

    public WeatherDataCacheKey(String zipcode, TimeZone timezone) {
        this.zipcode = zipcode;
        this.timezone = timezone;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public TimeZone getTimezone() {
        return this.timezone;
    }

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

    public int hashCode() {
        String hashcode = getTimezone().toString() + getZipcode();
        return hashcode.hashCode();
    }

}
