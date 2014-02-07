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
 * along with Weather.Next.  If not, see <http ://www.gnu.org/licenses/>.
 */
package net.chaosserver.weathernext.weather;

/**
 * Enumeration of the standard weather codes.
 * 
 * @author jreed
 * 
 */
public enum WeatherState {
    CLEAR, CLOUDS, RAIN, THUNDERSTORM, DRIZZLE, SNOW, ATMOSPHERE, EXTREME, UNKNOWN;

    public String toString() {
        switch (this) {
            case CLEAR:
                return "CLEAR";
            case CLOUDS:
                return "CLOUDS";
            case RAIN:
                return "RAIN";
            case THUNDERSTORM:
                return "THUNDERSTORM";
            case DRIZZLE:
                return "DRIZZLE";
            case SNOW:
                return "SNOW";
            case ATMOSPHERE:
                return "ATMOSPHERE";
            case EXTREME:
                return "EXTREME";
            case UNKNOWN:
                return "UNKNOWN";
            default:
                throw new IllegalArgumentException();
        }
    }
}
