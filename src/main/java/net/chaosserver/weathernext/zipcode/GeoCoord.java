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
    protected float longitude;
    protected float latitude;

    public GeoCoord() {
    }

    /**
     * Converts strings into the proper formats.
     * 
     * @param longitude
     * @param latitude
     * @throws NumberFormatException if a non-numberic string is supplied
     */
    public GeoCoord(String longitude, String latitude)
            throws NumberFormatException {
        this(Float.parseFloat(longitude), Float.parseFloat(latitude));
    }

    public GeoCoord(float longitude, float latitude) {
        setLongitude(longitude);
        setLatitude(latitude);
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLatitude() {
        return this.latitude;
    }

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
