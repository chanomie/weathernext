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

/**
 * POJO that holds coordinates.
 * 
 * @author jreed
 */
public class GeoCoord {
    /** The latitude. */
    protected float longitude;

    /** The latitude. */
    protected float latitude;

    /**
     * Converts strings into the proper formats.
     * 
     * @param longitude the value of longitude
     * @param latitude the value of latitude
     * @throws NumberFormatException if a non-numberic string is supplied
     */
    public GeoCoord(String longitude, String latitude)
            throws NumberFormatException {
        this(Float.parseFloat(longitude), Float.parseFloat(latitude));
    }

    /**
     * Constructs a new object using the given longitude and lattidue.
     * 
     * @param longitude the value of longitude
     * @param latitude the value of latitude
     */
    public GeoCoord(float longitude, float latitude) {
        setLongitude(longitude);
        setLatitude(latitude);
    }

    /**
     * Sets the longitude.
     * 
     * @param longitude the new value
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the longitude.
     * 
     * @return the longitude
     */
    public float getLongitude() {
        return this.longitude;
    }

    /**
     * Sets the latitude.
     * 
     * @param latitude the new value
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the latitude.
     * 
     * @return latitude
     */
    public float getLatitude() {
        return this.latitude;
    }

    /**
     * Checks if two objects are equal.
     * 
     * @param o object to compare to
     * @return if objects are equal
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof GeoCoord) {
            GeoCoord geoCoord2 = (GeoCoord) o;
            if (this.getLatitude() == geoCoord2.getLatitude()
                    && this.getLongitude() == geoCoord2.getLatitude()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns a hashcode for the geocoordinates.
     * 
     * @return a hashcode
     */
    public int hashCode() {
        String hashcode = String.valueOf(this.getLatitude())
                + String.valueOf(this.getLongitude());

        return hashcode.hashCode();
    }

    /**
     * Generates a debug string version of this object.
     * 
     * @return debug string
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(this.getClass().getName());
        result.append("[");
        result.append("longitude=");
        result.append(getLongitude());
        result.append(",latitude=");
        result.append(getLatitude());
        result.append("]");

        return result.toString();
    }
}
