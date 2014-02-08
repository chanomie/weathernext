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

import java.util.TimeZone;

/**
 * Interface for weather services that can return weather and forecast for a
 * particular zip code.
 * 
 * @author jreed
 */
public interface WeatherService {
    /**
     * Returns the weather and forecast for the given zipcode with dates
     * localized to the timezone.
     * 
     * @param zipcode zipcode to get weather for
     * @param timeZone timezone for localization
     * @return the weather and forecast
     */
    public WeatherData getWeather(String zipcode, TimeZone timeZone);
}
