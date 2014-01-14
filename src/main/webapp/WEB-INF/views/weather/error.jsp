<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<title>Weather.Next Error</title>
        <meta name="viewport" content="user-scalable=no, width=device-width, initial-scale=1">
	</head>
	<body bgcolor="#FF0000" style="background-color:#FF0000; margin: 0px; font-family: Arial; color: white; text-shadow: 0px 0px 4px rgba(0, 0, 0, 0.5); min-width: 100%">
		<table cellspacing="0" cellpadding="0" align="center" background="<c:out value="${prefix}"/>/imgs/bg/error.png" style="margin: 0px auto; text-align:center; width: 100%; max-width: 640px;">
			<tr style="height:20px"><td colspan="2">&nbsp;</td></tr>
			<tr style="font-size: 2.5em; height: 50px;"><td colspan="2">Oh no'es!</td></tr>
			<tr style="font-size: 1em; height: 20px;"><td colspan="2">&nbsp;</td></tr>
			<tr style="font-size: 1em; height: 20px;"><td colspan="2">&nbsp;</td></tr>
			<tr style="height: 70px;">
				<td width="50%" style="font-size: 3em; width: 50%;">&uarr; ??&#176;</td>
				<td width="50%" style="font-size: 3em; width: 50%; color: lightblue;">&darr; ??&#176;</td>
			</tr>
			<tr style="height: 200px;">
				<td colspan="2" style="vertical-align: top;" valign="top">
					<table cellspacing="0" cellpadding="0" style="width: 100%; text-align:center; ">
							<tr style="height: 40px;">
								<td style="font-size: 2em; width: 58%; border-top: dashed 1px black;">
									Unexpected atmospheric conditions are interfering with our weather robots!
								</td>
							</tr>
					</table>
				</td>
			</tr>
			<tr style="height: 70px;">
				<td colspan="2">&nbsp;</td>
			</tr>
		</table>
	</body>
</html>