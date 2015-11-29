$(document).ready(function() {
	(function() {
		var servletUrl = "http://localhost:8080/TwittMapApplication/WebServlet"; 
		var minutes, keyword;
		var points = [];
		var map;

		function sendMsg() {
			var data = {
				"minutes": minutes,
				"keyword": keyword
			};
			$.get(servletUrl, data, function(resp) {
				datas = resp.split("\n");
				points = [];
				for (var i = 0; i < datas.length; i++) {
					update(datas[i]);
				}
				heatmap = new google.maps.visualization.HeatmapLayer({
				    data: points,
				    map: map
				});
			})
			.fail(function() {
				console.log("fail");
			});
		}

		function update(data) {
			if (data != "start" && data != "end" && data != "no_matching") {
				var pointData = JSON.parse(data);
				points.push(new google.maps.LatLng(pointData.latitude, pointData.longitude));
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
			minutes = "-1";
			keyword = "all";
			sendMsg();
		}

		google.maps.event.addDomListener(window, 'load', initialize);
	})();
});
