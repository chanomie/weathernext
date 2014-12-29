# Weather.Next

A simple service to email you a weather forecast each day - with an email that is
formatted for mobile and doesn't contain 50% ads.

You think there are other services that do this? You're wrong. 
I tried them all and they are terrible, go look at my comparisons.


## Development

```
export JAVA_HOME=`/usr/libexec/java_home -v 1.7`
mvn verify - verify the build
mvn appengine:devserver - run the dev server
mvn eclipse:eclipse
mvn appengine:update
```