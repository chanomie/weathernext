package net.chaosserver.weathernext.weather;

public enum WeatherState {
	CLEAR,
    CLOUDS,
    RAIN,
    THUNDERSTORM,
    DRIZZLE,
    SNOW,
    ATMOSPHERE,
    EXTREME,
    UNKNOWN;
    
    public String toString() {
        switch(this) {
	        case CLEAR: return "CLEAR";
	        case CLOUDS: return "CLOUDS";
	        case RAIN: return "RAIN";
	        case THUNDERSTORM: return "THUNDERSTORM";
	        case DRIZZLE: return "DRIZZLE";
	        case SNOW: return "SNOW";
	        case ATMOSPHERE: return "ATMOSPHERE";
	        case EXTREME: return "EXTREME";
	        case UNKNOWN: return "UNKNOWN";
	        default: throw new IllegalArgumentException();
        }		
	}
}
