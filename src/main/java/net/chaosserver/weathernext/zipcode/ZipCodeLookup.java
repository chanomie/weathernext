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
    Map<String, LocationData> zipCodeData = new HashMap<String, LocationData>();
    private final static Logger logger = Logger.getLogger(ZipCodeLookup.class
            .getName());

    public ZipCodeLookup() {
        HeaderColumnNameMappingStrategy<LocationData> strat = new HeaderColumnNameMappingStrategy<LocationData>();
        strat.setType(LocationData.class);
        CsvToBean<LocationData> bean = new CsvToBean<LocationData>();
        try {
            Reader zipCodeListReader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream("/zipcodes/zipcodes.csv"), "UTF-8"));
            List<LocationData> locationList = bean.parse(strat, zipCodeListReader);
            for (LocationData locationData : locationList) {
                zipCodeData.put(locationData.getZipCode(), locationData);
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING, "Failed to open reader with UTF-8", e);
        }
    }

    public LocationData getLocationDate(String zipcode) {
        return zipCodeData.get(zipcode);
    }

    public GeoCoord getGeoCoord(String zipcode) {
        GeoCoord result = null;
        LocationData locationData = zipCodeData.get(zipcode);
        if (locationData != null) {
            try {
                result = new GeoCoord(locationData.getLongitude(),
                        locationData.getLatitude());
            } catch (NumberFormatException e) {
                logger.log(
                        Level.WARNING,
                        "Failed to parse coordinates for zip [" + zipcode + "]",
                        e);
                // Couldn't make it.
            }
        }

        return result;
    }
}
