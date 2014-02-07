package net.chaosserver.weathernext.zipcode;

import org.junit.Test;

public class ZipCodeLookupTest {
    // @Test
    public void testZipCodeLookup() throws Exception {
        ZipCodeLookup zipCodeLookup = new ZipCodeLookup();
        GeoCoord location = zipCodeLookup.getGeoCoord("95608");
        System.out.println("Location: " + location);
    }
}
