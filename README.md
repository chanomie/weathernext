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

```
export forecastkey=<yourkey>
export JAVA_HOME=`/usr/libexec/java_home -v 1.7`
mvn verify - verify the build
mvn appengine:devserver - run the dev server
mvn eclipse:eclipse
mvn appengine:update
```