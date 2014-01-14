/**
 * Says if the zipcode should be automatically set based on the geocordinates.
 * @type {boolean}
 * @private
 */
var autosetZip = false;

/**
 * The default zipcode to use if nothing is available
 * @define {string}
 * @private
 */
var defaultZip = "94111";

var showSchedule = false;

$( document ).ready(function() {
	var hash = window.location.hash.substring(1);
	if(hash === "schedule") {
		showSchedule = true;
	}
	
	initializeZipCode();
	checkGoogleAuth();
	$("#findlocation").click(startLocationLookup);
	$("#zipcodeform").submit(submitZipCode);
});

/**
 * Checks the Google Authentication Status by making a JSON call tot he status
 * API.
 */
function checkGoogleAuth() {
    $.ajax({
      url: "/api/v1/google/status?returnPath=" + encodeURIComponent(document.location),
      success: checkGoogleAuthSuccess,
      error: genericError
    });
}

/**
 * Success result from the Check Google auth function.  This will update the Google
 * login screen as appropriate.
 */
function checkGoogleAuthSuccess(data, status) {
	if(data.googleLoginStatus == "true") {
		$("#recipientemail").html(data.recipientEmail);
		$("#recipientemail").attr("href", data.googleLogoutUrl);
		$("#schedule").click(function() {
			buildSchedulePage();
			$("#mainpage").slideUp('slow');
			$("#schedulepage").slideDown('slow');			
		});
		$("#scheduleback").click(function() {
			$("#schedulepage").slideUp('slow');
			$("#mainpage").slideDown('slow');			
		});
    	googleAuth = true;
    } else {
		$("#schedule").click(function() {
			window.location.replace(data.googleLoginUrl);
		});    	
    	googleAuth = false;
	}
	
	if(showSchedule) {
		$("#schedule").click();
	}
}

/**
 * Sets the zipcode input on the screen.  This is called on the initial page
 * load to populate an initial zipcode and page.
 */
function initializeZipCode() {
	var lastZipcode = localStorage.getItem("lastZipcode");
	// 1) Check server side storage
	
	// 2) Check local storage
	if(lastZipcode) {
		$("#zipcode").val(lastZipcode);
		updateWeatheriFrame();
	} else {
		autosetZip = true;
		$("#zipcode").val(defaultZip);
		updateWeatheriFrame();
		consolelog("Making get location call");
		startLocationLookup();
	}
}

/**
 * Triggers a location lookup that will populate the zipcode and
 * reload the iframe.
 */
function startLocationLookup() {
	consolelog("Starting location lookup");
	navigator.geolocation.getCurrentPosition(getLocationFromGeo);
}

/**
 * Callback function for when a location is pulled from the browser.
 * It will parse out the lat/long and initiate the call to Google Geo API
 * to get the zipcode and fill in the form.
 * 
 * @param location {Object} the location object
 */
function getLocationFromGeo(location) {
	var latlng = location.coords.latitude + "," + location.coords.longitude,
	    geoUrl = "http://maps.googleapis.com/maps/api/geocode/json?components=postal_code&sensor=true&latlng="
	    	+ latlng;

	consolelog("Making call to : " + geoUrl);
	
	$.ajax({
	    url: geoUrl,
	    success: googleGeoApiSuccess,
	    error: genericError
	  });	
}

/**
 * Gets the result form the Google API call, parses out the zipcode and
 * fills in the form.
 * 
 * @param data {Object} the JSON object with the result data
 * @param status {string} the stats
 */
function googleGeoApiSuccess(data, status) {
	var result,
	    resultsIndex, 
	    addressComponentsIndex,
	    zipcodeResult;
	
	zipcodeloop: for (resultsIndex=0; resultsIndex<data.results.length; resultsIndex++) {
		result = data.results[resultsIndex];
		for(addressComponentsIndex = 0; addressComponentsIndex < result.address_components.length; addressComponentsIndex++) {
			if ($.inArray("postal_code", result.address_components[addressComponentsIndex].types) >= 0) {
				zipcodeResult = result.address_components[addressComponentsIndex].long_name;
				consolelog("Geo API returned zipcode: " + zipcodeResult);
				setZipcode(zipcodeResult);
				break zipcodeloop;
			}
		}
	}
}

/**
 * Form submit event if a user enters a zipcode on the main page and presses [enter]
 * @param event {Object} the form submit event
 */
function submitZipCode(event) {
	setZipcode($("#zipcode").val());
	event.preventDefault();
}

/**
 * Sets the zipcode into the form entry field and reloads the weather iframe
 * 
 * @param zipcode {string} the zipcode to set and reload the iframe if it has changed.
 */
function setZipcode(zipcode) {
	var currentZipcode;
	
	if($('#weatherframe').length > 0) {
		currentZipcode = $("#weatherframe").attr("data-zipcode");
	}
	
	if(currentZipcode !== zipcode) {
		$("#zipcode").val(zipcode);
		localStorage.setItem("lastZipcode", zipcode);
		updateWeatheriFrame();
	} else {
		consolelog("no change to the zipcode");
	}
}

/**
 * Updates the weather iframe based on the value in the input field.
 */
function updateWeatheriFrame() {
	var zipcode = $("#zipcode").val(),
	    timezoneString = getTimezoneOffset(),
	    iframeUrl,
	    iframeDiv;
	
	consolelog("Loading iframe for zip [" + zipcode + "], timezone [" + timezoneString + "]");
	iframeUrl = "weather?zip=" + encodeURIComponent(zipcode) + "&timezone=" + encodeURIComponent(timezoneString);
	
	iframeDiv = $("<iframe/>").
		addClass("weatherframe").
		attr("id", "weatherframe").
		attr("src", iframeUrl).
		attr("data-zipcode", zipcode);
	
	iframeDiv.load(function() {
		$("#weatherframe").height($("#weatherframe").contents().find("html").height());
	});
	
	$("#weatherframediv").
		empty().
		append(iframeDiv);
}

/**
 * Gets the current timezone offset
 */
function getTimezoneOffset() {
    var timezoneOffset = new Date().getTimezoneOffset(),
        minuteOffset = timezoneOffset%60;
    
    if(minuteOffset < 10) {
		minuteOffset = "0" + minuteOffset;
	}
	
	if(timezoneOffset > 0) {
		timezoneString = "GMT-" + Math.floor(timezoneOffset/60) + ":" + minuteOffset;
	} else {
		timezoneString = "GMT+" + Math.floor(timezoneOffset/60) + ":" + minuteOffset;
	}
	
	return timezoneString;
}

/**
 * Builds the schedule page.
 */
function buildSchedulePage() {
	$.ajax({
	    url: "/api/schedule",
	    success: getScheduleSuccess,
	    error: genericError
	  });
}

function getScheduleSuccess(data, status) {
	var scheduleIndex,
		sendTimeHour,
		selectBox;
	
	consolelog(JSON.stringify(data));
	
	$("#scheduletable").empty();
	for(scheduleIndex = 0; scheduleIndex < data.length; scheduleIndex ++) {
		sendTimeHour = new Date(parseInt(data[scheduleIndex].nextSend)).getHours()+":00";
		selectBox = getSelectBox(data[scheduleIndex].zipcode, sendTimeHour);
		$("#scheduletable").append(
				$("<tr/>").
					addClass("zipcoderow").
					attr("data-sendhour",sendTimeHour).
					attr("data-zipcode",data[scheduleIndex].zipcode).
					append($("<td/>").
						append($("<input/>").
							attr("size","5").
							attr("type","text").
							attr("name","zipcode"+scheduleIndex).
							attr("id","zipcode"+scheduleIndex).
							attr("readonly","readonly").
							addClass("centertext").
							addClass("zipcodetext").
							val(data[scheduleIndex].zipcode))).
					append($("<td/>").
						append(selectBox)).
					append($("<td/>").
							addClass("actionButton").
							append($("<i/>").
									addClass("fa").
									addClass("fa-trash-o").
									addClass("deleteSchedule").
									attr("data-key",data[scheduleIndex].key)
									))
		);				
	}
	
	appendNewScheduleRow();
	
	$(".deleteSchedule").click(deleteSchedule);
	$(".timeSelect").change(updateSchedule);
}

function appendNewScheduleRow() {
	selectBox = getSelectBox("newtime", new Date().setHours(18));
	$("#scheduletable").append(
			$("<tr/>").
				attr("id","newziprow").
				addClass("zipcoderow").
				append($("<td/>").
					append($("<input/>").
						attr("size","5").
						attr("type","text").
						attr("name","newzipcode").
						attr("id","newzipcode").
						addClass("zipcodetext").
						addClass("centertext"))).
				append($("<td/>").
					append(selectBox)).
				append($("<td/>").
						addClass("actionButton").
						append($("<i/>").
								addClass("fa").
								addClass("fa-plus").
								addClass("addSchedule").
								click(addSchedule)
								))
	);	
}

function deleteSchedule() {
	var scheduleKey = $(this).attr("data-key");
	consolelog("deleteSchedule");
	consolelog("scheduleKey: [" + scheduleKey + "]");
	$(this).closest("tr").remove();
	
	$.ajax({
		url: "/api/schedule/"+scheduleKey,
		type: "DELETE",
		error: genericError
    });	
}

function addSchedule() {
	var zipcode = $(this).closest("tr").find(".zipcodetext").val(),
    schedule = $(this).closest("tr").find(".timeSelect").val();

	consolelog("addSchedule");
	consolelog("zipcode: [" + zipcode + "], schedule =[" + schedule + "]");
	
	// so some validation stuff
	if(zipcode === null) {
		$(this).closest("tr").find(".zipcodetext").addClass("error");
	} else if (!(zipcode.match(/^\d{5}$/))) {
		$(this).closest("tr").find(".zipcodetext").addClass("error");
	} else if ( $("input[class='zipcodetext'][value='"+zipcode+"']").length > 1 ) {
		$(this).closest("tr").find(".zipcodetext").addClass("error");
	} else {
		$(this).closest("tr").find(".zipcodetext").removeClass("error");
		$(this).closest("tr").find(".zipcodetext").attr("readonly","readonly");
		// Change Add to Delete
		$(this).closest("tr").removeAttr("id");
		$(this).closest("tr").find(".actionButton").empty();
		
		/*
		// This should occur on callback of add so that the datakey
		//  can be written properly.
		$(this).closest("tr").find(".actionButton").
			append(
				$("<i/>").
					addClass("fa").
					addClass("fa-trash-o").
					addClass("deleteSchedule").
					attr("data-key",scheduleKey).
					click(deleteSchedule)
				);
		*/
				
		appendNewScheduleRow();
		updateServerSchedule(zipcode, schedule);
	}
}


function updateSchedule() {
	var zipcode = $(this).closest("tr").find(".zipcodetext").val(),
        schedule = $(this).closest("tr").find(".timeSelect").val();

	consolelog("updateSchedule");
	consolelog("zipcode: [" + zipcode + "], schedule =[" + schedule + "]");
	
	if($(this).closest("tr").attr("id") !== "newziprow") {
		updateServerSchedule(zipcode, schedule);
	} else {
		consolelog("don't update on new entry")
	}
}

function updateServerSchedule(zipcode, schedule) {
	var scheduleTime = (new Date()).setHours(schedule.match(/^\d{1,2}/)),
	    timezoneString = getTimezoneOffset();
	
	$.ajax({
		url: "/api/schedule",
		type: "POST",
		data: { "zip": zipcode, "timezone": timezoneString , "sendTime": scheduleTime},
		error: genericError
    });
}

function getSelectBox(zip, sendTimeHour) {
	var selectBox;

	selectBox = $("<select/>").
		append($("<option/>").attr("value","1:00").html("1am")).
		append($("<option/>").attr("value","2:00").html("2am")).
		append($("<option/>").attr("value","3:00").html("3am")).
		append($("<option/>").attr("value","4:00").html("4am")).
		append($("<option/>").attr("value","5:00").html("5am")).
		append($("<option/>").attr("value","6:00").html("6am")).
		append($("<option/>").attr("value","7:00").html("7am")).
		append($("<option/>").attr("value","8:00").html("8am")).
		append($("<option/>").attr("value","9:00").html("9am")).
		append($("<option/>").attr("value","10:00").html("10am")).
		append($("<option/>").attr("value","11:00").html("11am")).
		append($("<option/>").attr("value","12:00").html("12pm")).
		append($("<option/>").attr("value","13:00").html("1pm")).
		append($("<option/>").attr("value","14:00").html("2pm")).
		append($("<option/>").attr("value","15:00").html("3pm")).
		append($("<option/>").attr("value","16:00").html("4pm")).
		append($("<option/>").attr("value","17:00").html("5pm")).
		append($("<option/>").attr("value","18:00").html("6pm")).
		append($("<option/>").attr("value","19:00").html("7pm")).
		append($("<option/>").attr("value","20:00").html("8pm")).
		append($("<option/>").attr("value","21:00").html("9pm")).
		append($("<option/>").attr("value","22:00").html("10pm")).
		append($("<option/>").attr("value","23:00").html("11pm")).
		append($("<option/>").attr("value","00:00").html("12am")).
		val(sendTimeHour).
		addClass("timeSelect").
		attr("data-zipcode",zip);
	
	return selectBox;
}


function consolelog(logMessage) {
	console.log(logMessage);
}

/**
 * Handles a generic AJAX error.
 *
 * @param jqXHR {Object} the xhr response object
 * @param textStatus {string} The text response of the error.
 */
function genericError(jqXHR, textStatus) {
	alert("Failure: textStatus = [" + textStatus + "], jqXHR.response [" + JSON.stringify(jqXHR.response) + "]");
	console.log("Failure: textStatus = [" + textStatus + "], jqXHR.response [" + JSON.stringify(jqXHR.response) + "]");
}