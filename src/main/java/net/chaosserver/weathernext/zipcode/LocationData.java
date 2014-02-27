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
package net.chaosserver.weathernext.zipcode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A POJO of City Data.
 * 
 * @author jreed
 * 
 */
public class LocationData {
    // "zip_code","latitude","longitude","city","state","county"

    /** The 5-digit postal code. */
    protected String zipCode;

    /** The latitude. */
    protected String latitude;

    /** The longitude. */
    protected String longitude;

    /** The city. */
    protected String city;

    /** The 2-character state. */
    protected String state;

    /** The county. */
    protected String county;

    /**
     * Used for population as a JavaBean.
     */
    public LocationData() {
    }

    /**
     * Constructs the LocationData object with all values set.
     * 
     * @param zipCode the zipcode
     * @param latitude the latitude of the object
     * @param longitude the longitude of the object
     * @param city the city the location is in
     * @param state the state the location is in
     * @param county the country the location is in
     */
    public LocationData(String zipCode, String latitude, String longitude,
            String city, String state, String county) {
        setZipCode(zipCode);
        setLatitude(latitude);
        setLongitude(longitude);
        setCity(city);
        setState(state);
        setCounty(county);
    }

    /**
     * Sets the zipcode.
     * 
     * @param zipCode new value
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the zipcode.
     * 
     * @return the zipcode
     */
    public String getZipCode() {
        return this.zipCode;
    }

    /**
     * Sets the latitude.
     * 
     * @param latitude the new value
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the latitude.
     * 
     * @return the latitude
     */
    public String getLatitude() {
        return this.latitude;
    }

    /**
     * Sets the longitude.
     * 
     * @param longitude the new value
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the longitude.
     * 
     * @return the new value
     */
    public String getLongitude() {
        return this.longitude;
    }

    /**
     * Sets the city.
     * 
     * @param city the new value
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the city.
     * 
     * @return the city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Sets the state.
     * 
     * @param state the new value
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets the country.
     * 
     * @param county the new value
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Gets the country.
     * 
     * @return the new value
     */
    public String getCounty() {
        return this.county;
    }

    /**
     * Returns a hashbcode for this object.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(zipCode).append(latitude)
                .append(longitude).append(city).append(state).append(county)
                .toHashCode();

    }

    /**
     * Returns if two objects are equal.
     * 
     * @param o other object to compare
     * @return if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof LocationData) {
            LocationData other = (LocationData) o;
            return new EqualsBuilder().append(zipCode, other.zipCode)
                    .append(latitude, other.latitude)
                    .append(longitude, other.longitude)
                    .append(city, other.city).append(state, other.state)
                    .append(state, other.state)
                    .append(latitude, other.latitude).isEquals();
        } else {
            return false;
        }
    }
}
