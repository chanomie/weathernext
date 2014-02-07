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
        if(!(o instanceof WeatherDataCacheKey)) {
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
