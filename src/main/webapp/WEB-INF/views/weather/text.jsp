<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:out value="${weatherData.locationName}"/>
<c:out value="${weatherData.weatherDescription}"/>
High <fmt:formatNumber type="number" maxFractionDigits="0" value="${weatherData.highTempurature}" />, Low <fmt:formatNumber type="number" maxFractionDigits="0" value="${weatherData.lowTempurature}" />
<fmt:formatDate pattern="EEEEEE, MM dd, yyyy" value="${weatherData.day}" />
=======================
<c:forEach var="forecastDay" items='${weatherData.forecast}'><fmt:formatDate pattern="EEEEEE, MMM dd, yyyy" value="${forecastDay.day}" />
<c:out value="${forecastDay.weatherDescription}" />
High <fmt:formatNumber type="number" maxFractionDigits="0" value="${forecastDay.highTempurature}" />, Low <fmt:formatNumber type="number" maxFractionDigits="0" value="${forecastDay.lowTempurature}" />
=======================
</c:forEach>