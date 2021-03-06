<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setTimeZone value="${timezone.ID}" />
<html>
	<head>
		<title>Weather.Next</title>
        <meta name="viewport" content="user-scalable=no, width=device-width, initial-scale=1">
        <c:if test="${not emailformat}">
          <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
          <script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
          <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
            <script>
                $(function() {
                    $( document ).tooltip();
                });
            </script>
            <style>
                .ui-tooltip-content { text-shadow: none; }
            </style>
        </c:if>
		<style>
		  /* table, th, td { border: 1px solid black; } */
		  .appleWhiteLinks a { color: #FFFFFF !important; text-decoration: none; }
		</style>
	</head>
	<c:choose>
	    <c:when test="${weatherData.weatherState == 'CLEAR'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/clear.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/clear.png"/>
	    	<c:set var="backgroundColor" value="#1C5FCE"/>
	    	<c:set var="unicodeIcon" value="&#9728;"/>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'CLOUDS'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/clouds.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/clouds.png"/>
	    	<c:set var="backgroundColor" value="#9EA1A0"/>
	    	<c:set var="unicodeIcon" value="&#9729;"/>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'RAIN'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/rain.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/rain.png"/>
	    	<c:set var="backgroundColor" value="#817d7c"/>
	    	<c:set var="unicodeIcon" value="&#9748;"/><%-- Umbrella --%>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'THUNDERSTORM'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/thunderstorm.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/thunderstorm.png"/>
	    	<c:set var="backgroundColor" value="#4e4d93"/>
	    	<c:set var="unicodeIcon" value="&#9889;"/>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'DRIZZLE'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/drizzle.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/drizzle.png"/>
	    	<c:set var="backgroundColor" value="#96b2b3"/>
	    	<c:set var="unicodeIcon" value="&#9730;"/>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'SNOW'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/snow.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/snow.png"/>
	    	<c:set var="backgroundColor" value="#668aae"/>
	    	<c:set var="unicodeIcon" value="&#9730;"/>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'ATMOSPHERE'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/atmosphere.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/atmosphere.png"/>
	    	<c:set var="backgroundColor" value="#d6d6d6"/>
	    	<c:set var="unicodeIcon" value="&#9832;"/>
	    </c:when>
	    <c:when test="${weatherData.weatherState == 'EXTREME'}">
	    	<c:set var="conditionBackground" value="/imgs/bg/extreme.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/extreme.png"/>
	    	<c:set var="backgroundColor" value="#d17576"/>
	    	<c:set var="unicodeIcon" value="&#127755;"/>
	    </c:when>
	    <c:otherwise>
	    	<c:set var="conditionBackground" value="/imgs/bg/unknown.png"/>
	    	<c:set var="conditionIcon" value="/imgs/icon/unknown.png"/>
	    	<c:set var="backgroundColor" value="#1C5FCE"/>
	    	<c:set var="unicodeIcon" value="&#128163;"/><%-- ??? --%>
	    </c:otherwise>
	</c:choose>
    <c:choose>
        <c:when test="${fn:length(weatherDescription) gt 25}">
            <c:set var="sunSchedule"><c:if test="${not empty weatherData.sunrise}">, Rise <fmt:formatDate pattern="h:mm a" value="${weatherData.sunrise}" /></c:if><c:if test="${not empty weatherData.sunset}">, Set <fmt:formatDate pattern="h:mm a" value="${weatherData.sunset}" /></c:if></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="sunSchedule"><c:if test="${not empty weatherData.sunrise}">, Sunrise <fmt:formatDate pattern="h:mm a" value="${weatherData.sunrise}" /></c:if><c:if test="${not empty weatherData.sunset}">, Sunset <fmt:formatDate pattern="h:mm a" value="${weatherData.sunset}" /></c:if></c:set>
        </c:otherwise>
    </c:choose>	
    <c:choose>
	    <c:when test="${weatherData.moonPhase == 'FULL'}">
	    	<c:set var="moonIcon" value="/imgs/moon/full.png"/>
	    	<c:set var="moonDescription" value="Full Moon"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'WAXING_GIBBOUS'}">
	    	<c:set var="moonIcon" value="/imgs/moon/waxing_gibbous.png"/>
	    	<c:set var="moonDescription" value="Waxing Gibbous"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'FIRST_QUARTER'}">
	    	<c:set var="moonIcon" value="/imgs/moon/first_quarter.png"/>
	    	<c:set var="moonDescription" value="First Quarter"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'WAXING_CRESCENT'}">
	    	<c:set var="moonIcon" value="/imgs/moon/waxing_crescent.png"/>
	    	<c:set var="moonDescription" value="Waxing Crescent"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'NEW'}">
	    	<c:set var="moonIcon" value="/imgs/moon/new.png"/>
	    	<c:set var="moonDescription" value="New Moon"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'WANING_CRESCENT'}">
	    	<c:set var="moonIcon" value="/imgs/moon/waning_crescent.png"/>
	    	<c:set var="moonDescription" value="Waning Crescent"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'THIRD_QUARTER'}">
	    	<c:set var="moonIcon" value="/imgs/moon/third_quarter.png"/>
	    	<c:set var="moonDescription" value="Third Quarter"/>
	    </c:when>
	    <c:when test="${weatherData.moonPhase == 'WANING_GIBBOUS'}">
	    	<c:set var="moonIcon" value="/imgs/moon/waning_gibbous.png"/>
	    	<c:set var="moonDescription" value="Waning Gibbous"/>
	    </c:when>
    </c:choose>
	<!--  height: 920 -->
	<body bgcolor="<c:out value="${backgroundColor}"/>" style="background-color:<c:out value="${backgroundColor}"/>; margin: 0px; font-family: Arial; color: white; text-shadow: 0px 0px 4px rgba(0, 0, 0, 0.5); width: 100%; min-width: 100%">
		<div style="font-size: 0em; width: 100%; text-shadow:none; color: <c:out value="${backgroundColor}"/>"><c:out value="${weatherDescription}"/><c:out value="${sunSchedule}"/></div>
		<table cellspacing="0" cellpadding="0" align="center" background="<c:out value="${prefix}"/><c:out value="${conditionBackground}"/>" style="margin: 0px auto; text-align:center; width: 100%; max-width: 620px;">
			<tr style="height:20px"><td colspan="2">&nbsp;</td></tr>
			<tr style="font-size: 2.5em; height: 50px;"><td colspan="2"><a style="color: #FFFFFF !important; text-decoration: none;" href="darksky://<c:out value="${zipcode}"/>"><c:out value="${weatherData.locationName}"/></a></td></tr>
			<tr style="font-size: 1em; height: 20px;"><td colspan="2"><span class="appleWhiteLinks"><fmt:formatDate pattern="EEEEEE, MMMM dd" 
            value="${weatherData.day}" /></span></td></tr>
			<tr style="font-size: 1em; height: 20px;"><td colspan="2"><c:out value="${weatherData.weatherDescription}"/><img src="<c:out value="${prefix}"/><c:out value="${conditionIcon}"/>" style="vertical-align:middle; padding-left: 5px;" alt="${weatherData.weatherDescription}" title="${weatherData.weatherDescription}" height="20" width="20"/></td></tr>
			<c:if test="${not empty moonIcon}">
				<tr style="font-size: 1em; height: 20px;"><td colspan="2"><c:out value="${moonDescription}"/><img src="<c:out value="${prefix}"/><c:out value="${moonIcon}"/>" style="vertical-align:middle; padding-left: 5px;" alt="${moonDescription}" title="${moonDescription}" height="20" width="20"/></td></tr>
			</c:if>
			<tr style="height: 70px;">
				<td width="50%" style="font-size: 3em; width: 50%;">&uarr; <fmt:formatNumber type="number" maxFractionDigits="0" value="${weatherData.highTempurature}" />&#176;</td>
				<td width="50%" style="font-size: 3em; width: 50%; color: lightblue;">&darr; <fmt:formatNumber type="number" maxFractionDigits="0" value="${weatherData.lowTempurature}" />&#176;</td>
			</tr>
			<c:if test="${not empty weatherData.sunrise or not empty weatherData.sunset}">
				<tr style="height: 40px;">
					<td width="50%" style="font-size: 2em; width: 50%;">
						<c:choose>
							<c:when test="${empty weatherData.sunrise}">&nbsp;</c:when>
							<c:otherwise>
								<img src="<c:out value="${prefix}"/>/imgs/icon/sunrise.png" style="vertical-align:middle" alt="Sunrise" title="Sunrise" height="30" width="30"/>
								<span class="appleWhiteLinks"><fmt:formatDate pattern="h:mm a" value="${weatherData.sunrise}" /></span>
							</c:otherwise>
						</c:choose>
	            	</td>
					<td width="50%" style="font-size: 2em; width: 50%;">
						<c:choose>
							<c:when test="${empty weatherData.sunset}">&nbsp;</c:when>
							<c:otherwise>
								<img src="<c:out value="${prefix}"/>/imgs/icon/sunset.png" style="vertical-align:middle" alt="Sunset" title="Sunset" height="30" width="30"/>
								<span class="appleWhiteLinks"><fmt:formatDate pattern="h:mm a" value="${weatherData.sunset}" /></span>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:if>
			<tr style="height: 200px;">
				<td colspan="2" style="vertical-align: top;" valign="top">
					<table cellspacing="0" cellpadding="0" style="width: 100%; text-align:center; ">
						<c:forEach var="forecastDay" items='${weatherData.forecast}'>
						<c:choose>
						    <c:when test="${forecastDay.weatherState == 'CLEAR'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/clear.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'CLOUDS'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/clouds.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'RAIN'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/rain.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'THUNDERSTORM'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/thunderstorm.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'DRIZZLE'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/drizzle.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'SNOW'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/snow.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'ATMOSPHERE'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/atmosphere.png"/>
						    </c:when>
						    <c:when test="${forecastDay.weatherState == 'EXTREME'}">
						    	<c:set var="conditionIcon" value="/imgs/icon/extreme.png"/>
						    </c:when>
						    <c:otherwise>
						    	<c:set var="conditionIcon" value="/imgs/icon/unknown.png"/>
						    </c:otherwise>
						</c:choose>
							<tr style="height: 40px;">
								<td style="font-size: 2em; width: 58%; border-top: dashed 1px black; text-align: left; padding-left: 5px"><fmt:formatDate pattern="EEEEEE" value="${forecastDay.day}" /></td>
								<td style="font-size: 2em; width: 11%; border-top: dashed 1px black; text-align: left" align="left"><img src="<c:out value="${prefix}"/><c:out value="${conditionIcon}"/>" style="vertical-align:middle" alt="${forecastDay.weatherDescription}" title="${forecastDay.weatherDescription}" height="30" width="30"/></td>
								<td style="font-size: 2em; width: 15%; border-top: dashed 1px black;"><fmt:formatNumber type="number" maxFractionDigits="0" value="${forecastDay.highTempurature}" /></td>
								<td style="font-size: 2em; width: 15%; color: lightblue; border-top: dashed 1px black;"><fmt:formatNumber type="number" maxFractionDigits="0" value="${forecastDay.lowTempurature}" /></td>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tr style="height: 70px;">
				<td colspan="2" style="text-align:right; padding-right: 5px;">
				  <c:if test="${not empty weatherData.attributionString}">
				  	<c:if test="${not empty weatherData.attributionUrl}">
				      <a style="color: white !important;" href="<c:out value="${weatherData.attributionUrl}"/>"></c:if><c:out value="${weatherData.attributionString}"/><c:if test="${not empty weatherData.attributionUrl}"></a>
				    </c:if>				      
				  </c:if>
				  <c:if test="${not empty weatherData.attributionString and not empty skey}">
				    <br/>
				  </c:if>
				  <c:if test="${not empty skey}">
				    <a style="color: white !important;" href="<c:out value="${prefix}"/>/weather/unsubscribe/<c:out value="${skey}"/>">Unsubscribe</a>
				  </c:if>
				</td>
			</tr>
			<c:if test="${not empty triggerReasons}">
				<tr>
				    <td colspan="2" style="text-align:right; padding-right: 5px;">
				      <c:forEach var="triggerReason" items='${triggerReasons.triggerReasons}'>
				      	<p><fmt:formatDate pattern="EEEEEE, MMMM dd" value="${triggerReason.triggerDate}" /> - <c:out value="${triggerReason.triggerReason}"/></p>
				      </c:forEach>
				    </td>
				</tr>
			</c:if>
		</table>
	</body>
</html>