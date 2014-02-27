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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy;

/**
 * Provides a method of looking up a latitude and longitude based on a zipcode.
 * Zip code file comes from:
 * http://notebook.gaslampmedia.com/wp-content/uploads/
 * 2013/08/zip_codes_states.csv
 * 
 * @author jreed
 * 
 */
public class ZipCodeLookup {
    /** The logger. */
    private final static Logger logger = Logger.getLogger(ZipCodeLookup.class
            .getName());

    /**
     * This holds the zipCodeData lookup and is loaded during the objects
     * construction.
     */
    protected Map<String, LocationData> zipCodeData = new HashMap<String, LocationData>();

    /**
     * Constructs the nw zipcode object. This will load data out of a file into
     * an internal lookup. This object is designed to be held as a singleton or
     * other cached object.
     */
    public ZipCodeLookup() {
        HeaderColumnNameMappingStrategy<LocationData> strat = new HeaderColumnNameMappingStrategy<LocationData>();
        strat.setType(LocationData.class);
        CsvToBean<LocationData> bean = new CsvToBean<LocationData>();
        try {
            Reader zipCodeListReader = new BufferedReader(
                    new InputStreamReader(this.getClass()
                            .getResourceAsStream("/zipcodes/zipcodes.csv"),
                            "UTF-8"));
            List<LocationData> locationList = bean.parse(strat,
                    zipCodeListReader);
            for (LocationData locationData : locationList) {
                zipCodeData.put(locationData.getZipCode(), locationData);
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING, "Failed to open reader with UTF-8", e);
        }
    }

    /**
     * Gets the location data for a specific zipcode.
     * 
     * @param zipcode the zipcode to grab
     * @return location data for that zipcode.
     */
    public LocationData getLocationData(String zipcode) {
        return zipCodeData.get(zipcode);
    }

    /**
     * Given a zipcode, gets back the GeoCoordinates assocaited with it from the
     * lookup.
     * 
     * @param zipcode the zip to lookup
     * @return the resulting geocoordinates
     */
    public GeoCoord getGeoCoord(String zipcode) {
        GeoCoord result = null;
        LocationData locationData = zipCodeData.get(zipcode);
        if (locationData != null) {
            try {
                result = new GeoCoord(locationData.getLongitude(),
                        locationData.getLatitude());
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING,
                        "Failed to parse coordinates for zip [" + zipcode
                                + "]", e);
                // Couldn't make it.
            }
        }

        return result;
    }
}
