package net.chaosserver.weathernext.zipcode;

/**
 * A POJO of City Data
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
     * Used for serialization only.
     */
    public LocationData() {
    }

    public LocationData(String zipCode, String latitude, String longitude,
            String city, String state, String county) {
        setZipCode(zipCode);
        setLatitude(latitude);
        setLongitude(longitude);
        setCity(city);
        setState(state);
        setCounty(county);
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCounty() {
        return this.county;
    }
}
