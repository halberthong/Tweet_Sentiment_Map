$(document).ready(function() {
	(function() {
//		var servletUrl = "http://cloudyyyyy-3mamuu3rzh.elasticbeanstalk.com/WebServlet";
		var servletUrl = "http://localhost:8080/TwittMapApplication/WebServlet"; 
		var minutes, keyword;
		var websocket;
		var oldpoints = {};
		var newpoints = {};
		var map;
		var heapmap;

		function sendMsg() {
			var data = {
				"minutes": minutes,
				"keyword": keyword
			};
			$.get(servletUrl, data, function(resp) {
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
					addInfoWindow(marker, pointData.content);
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
			keyword = "all";
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

		function addInfoWindow(marker, message) {
			var infoWindow = new google.maps.InfoWindow({
				content: '<div class="infoWindow">`<p>' + message + '</p></div>'
			});
			google.maps.event.addListener(marker, 'click', function () {
				infoWindow.open(map, marker);
			});
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
			var key = pointData.keyword;
			if (key == "food") {
				return icons[0];
			} else if (key == "music") {
				return icons[1];
			} else if (key == "movie") {
				return icons[2];
			} else if (key == "sports") {
				return icons[3];
			} else if (key == "tech") {
				return icons[4];
			} else {
				return icons[0];
			}
		}

		$("#time-select").change(function() {
			minutes = document.getElementById("time-select").value;
			sendMsg();
		});

		$("#category-select").change(function() {
			keyword = document.getElementById("category-select").value;
			sendMsg();
		});

		google.maps.event.addDomListener(window, 'load', initialize);
		setInterval(function() {
			sendMsg();
		}, 1000 * 3);
	})();
});
