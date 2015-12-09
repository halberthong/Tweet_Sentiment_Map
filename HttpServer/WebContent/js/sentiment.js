$(document).ready(function() {
	(function() {
//		var servletUrl = "http://cloudyyyyy-3mamuu3rzh.elasticbeanstalk.com/WebServlet";
		var servletUrl = "http://localhost:8080/TwittMapApplication/WebSentimentServlet"; 
		var minutes;
		var websocket;
		var oldpoints = {};
		var newpoints = {};
		var map;
		var heapmap;

		function sendMsg() {
			var data = {
				"minutes": minutes,
			};
			$.get(servletUrl, data, function(resp) {
				console.log(resp);
				datas = resp.split("\n");
				for (var i = 0; i < datas.length; i++) {
					update(datas[i]);
				}
			})
			.fail(function() {
				console.log("fail");
			});
		}

		function update(data) {
			if (data == "start") {
				newpoints = {};
			} else if (data == "end") {
				removeMarkers();
			} else if (data == "no_matching"){
				clearMarkers();
			} else {
				var pointData = JSON.parse(data);
				var tmpKey = pointData.statusId;
				if (tmpKey in oldpoints) {
					newpoints[tmpKey] = oldpoints[tmpKey];
				} else {
					var curIcon = chooseIcon(pointData);
					var marker = new google.maps.Marker({
						position: new google.maps.LatLng(pointData.latitude, pointData.longitude),
						map: map,
						icon: curIcon
					});
					newpoints[tmpKey] = marker;
				}
			}
		}

		function initialize() {
			var initPosition = {
					lat: 0,
					lng: 0
			};
			var initOption = {
					zoom : 2,
					center: initPosition
			};
			map = new google.maps.Map(document.getElementById('map'), initOption);
			minutes = "60";
			sendMsg();
		}

		function removeMarkers() {
			for (var key in oldpoints) {
				if (key in newpoints) {
				} else {
					oldpoints[key].setMap(null);
				}
			}
			oldpoints = newpoints;
		}

		function clearMarkers() {
			for (var key in oldpoints) {
				oldpoints[key].setMap(null);
			}
			oldpoints = {};
		}

		var iconURLPrefix = 'http://maps.google.com/mapfiles/ms/icons/';

		var icons = [
		             iconURLPrefix + 'red-dot.png',
		             iconURLPrefix + 'orange-dot.png',
		             iconURLPrefix + 'yellow-dot.png',
		             iconURLPrefix + 'green-dot.png',
		             iconURLPrefix + 'blue-dot.png'
		             ];

		function chooseIcon(pointData) {
			var key = pointData.sentiment;
			if (key == "positive") {
				return icons[0];
			} else if (key == "negative") {
				return icons[4];
			} else {
				return icons[2];
			}
		}

		$("#time-select").change(function() {
			minutes = document.getElementById("time-select").value;
			sendMsg();
		});

		google.maps.event.addDomListener(window, 'load', initialize);
		setInterval(function() {
			sendMsg();
		}, 1000 * 2);
	})();
});