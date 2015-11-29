<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<title>TwittMap</title>
<link rel="stylesheet" href="styles/styles.css" type="text/css" media="screen">
<link rel="stylesheet" href="styles/bootstrap.min.css" type="text/css" media="screen">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script>
<script src="js/sentiment.js"></script>
</head>
<body>
	<div class="container">
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand" href="#">TwittMap</a>
				</div>
				<form class="navbar-form pull-left">
					<label for="time-select">Time Range</label> <select
						id="time-select" class="form-control" style="width: 150px;">
						<option value="1">Within 1 Minutes</option>
						<option value="5">Within 5 Minutes</option>
						<option value="10">Within 10 Minutes</option>
						<option value="30">Within 30 Minutes</option>
						<option selected="selected" value="60">Within A Hour</option>
					</select>
				</form>
				<ul class="nav navbar-nav navbar-right">
					<li><a role="button" href="http://localhost:8080/TwittMapApplication/index.jsp">Marker Map</a></li>
					<li><a role="button" href="http://localhost:8080/TwittMapApplication/heatmap.jsp">Heat Map</a></li>
					<li><a role="button" href="http://localhost:8080/TwittMapApplication/sentiment.jsp">Sentiment Map</a></li>
				</ul>
			</div>
		</nav>
		<div id="map"></div>
	</div>
</body>
</html>