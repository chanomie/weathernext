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

/**
 * Enumeration of the standard weather codes.
 * 
 * @author jreed
 * 
 */
public enum MoonPhase {
    /** Full Moon is for lunation between 0.9375-0.0625 . */
    FULL,
    /** Waxing Gibbous is for lunation between 0.0625-0.1875 . */
    WAXING_GIBBOUS,
    /** First Quarter is for lunation between 0.1875 0.3125 . */
    FIRST_QUARTER,
    /** Waxing Crescent is for lunation between 0.3125 0.4375 . */
    WAXING_CRESCENT,
    /** New Moon is for lunation between 0.4375 0.5625 . */
    NEW,
    /** Waning Crescent is for lunation between 0.5625 0.6875 . */
    WANING_CRESCENT,
    /** Third Quarter is for lunation between 0.6875 0.8125. */
    THIRD_QUARTER,
    /** Waning Gibbous is for lunation between 0.8125 0.9375. **/
    WANING_GIBBOUS, UNKNOWN;

    /**
     * Returns a string representation.
     * 
     * @return String representation
     */
    public String toString() {
        switch (this) {
            case FULL:
                return "FULL";
            case WAXING_GIBBOUS:
                return "WAXING_GIBBOUS";
            case FIRST_QUARTER:
                return "FIRST_QUARTER";
            case WAXING_CRESCENT:
                return "WAXING_CRESCENT";
            case NEW:
                return "NEW";
            case WANING_CRESCENT:
                return "WANING_CRESCENT";
            case THIRD_QUARTER:
                return "THIRD_QUARTER";
            case WANING_GIBBOUS:
                return "WANING_GIBBOUS";
            case UNKNOWN:
                return "UNKNOWN";
            default:
                throw new IllegalArgumentException();
        }
    }
}
