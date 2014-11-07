App Engine Java Application
Copyright (C) 2010-2012 Google Inc.

## Skeleton application for use with App Engine Java.

Requires [Apache Maven](http://maven.apache.org) 3.0 or greater, and JDK 6+ in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

For further information, consult the [Java App Engine](https://developers.google.com/appengine/docs/java/overview) documentation.

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine


export JAVA_HOME=`/usr/libexec/java_home -v 1.7`
mvn verify - verify the build
mvn appengine:devserver - run the dev server
mvn eclipse:eclipse
mvn appengine:update

curl -d "recipientName=Jordan Reed" -d "recipientEmail=jordan.reed@gmail.com" -d "zip=93514" -d "timezone=GMT-8:00" -d "sendTime=1388023200000" -d "sendLiteral=true" http://localhost:8080/api/schedule
curl -d "recipientName=Jordan Reed" -d "recipientEmail=jordan.reed@gmail.com" -d "zip=95608" -d "timezone=GMT-8:00" -d "sendTime=1388023200000" -d "sendLiteral=true" http://localhost:8080/api/schedule

curl -b "dev_appserver_login=test@example.com:true:18580476422013912411" http://localhost:8080/api/schedule
curl http://localhost:8080/api/schedule?showall=true
curl -X DELETE "http://localhost:8080/api/schedule/jordan.reed%40gmail.com%2F95608"
curl -d "scheduleKey=jordan.reed@gmail.com_95608" http://localhost:8080/api/schedule/delete


curl -b "dev_appserver_login=test@example.com:true:18580476422013912411" -d "recipientName=Jordan Reed" -d "recipientEmail=test@example.com" -d "zip=94111" -d "timezone=GMT-8:00" -d "sendTime=1388023200000" -d "weather=rain,thunderstorm,snow,extreme,unknown" -d "sendLiteral=true" http://localhost:8080/api/schedule
