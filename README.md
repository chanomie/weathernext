# Weather.Next

A simple service to email you a weather forecast each day - with an email that is
formatted for mobile and doesn't contain 50% ads.

You think there are other services that do this? You're wrong. 
I tried them all and they are terrible, go look at my comparisons.

Here is the fun marketing page:
http://chanomie.github.io/weathernext/

You can find it here:
http://weathernext.appspot.com

## Development
The system uses Forecast.io and needs to include the key in the build.  It attempts
to read it from the environment variable "forecastkey".

You may need to set your Java version to 1.8


```
export forecastkey=<yourkey>
export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
mvn verify - verify the build
mvn appengine:devserver - run the dev server
mvn eclipse:eclipse
mvn appengine:update
```

## Testing
You can test a trigger logged in as admin by requesting:
* http://localhost:9080/weather/scheduled

## Testing a specific weather trigger id:
http://localhost:9080/weather?zip=95608&timezone=GMT-7%3A00&triggerReasonId=687024894

You can trigger the current users schedules at
* http://localhost:9080/weather/scheduled/me