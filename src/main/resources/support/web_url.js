Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
var timeIntervalNow;
var createWebUrlDetailChart = function(url) {
	$('#myModal').modal({
				backdrop : false
			})
	$.ajax({
				url : "getUrlDetailData.do",
				data : {
					url : url,
					timeInterval : timeIntervalNow
				},
				success : function(data) {
					var count = [];
					var concurrentMax = [];
					for (var i = 0; i < data.content.data.length; i++) {
						count.push([data.content.data[i].timeStamp,
								data.content.data[i].webUrlStat.Count]);
						concurrentMax.push([data.content.data[i].timeStamp,
								data.content.data[i].webUrlStat.ConcurrentMax]);

					}

					var yLabelFormatter = function() {
						return this.value;
					}

					var urlParam = {
						title : url,
						renderTo : 'urlDetailDiv',
						yLabelFormatter : yLabelFormatter,
						series : [{
									name : '访问次数',
									data : count
								}, {
									name : '最大并发',
									data : concurrentMax
								}]
					};
					var urlOptions = buildOption(urlParam);
					new Highcharts.Chart(urlOptions);
				}
			});

	$.ajax({
				url : "getUrlErrorDetailData.do",
				data : {
					url : url
				},
				success : function(data) {
					$("#urlErrorDiv").html(data.content.data);
				}
			});
}
var showUrlProfile = function(url) {
	$.ajax({
				url : "getWebUrlProfileData.do",
				data : {
					url : url,
					timeInterval : timeIntervalNow
				},
				success : function(data) {
					$("#webUrlProfileDiv").html(data.content.data);
					$('#myTableModal').modal({
								backdrop : false
							})
				}
			});
}

$(document).ready(function() {

			// 绑定tab切换事件
			changeTab('timeChangeDiv', function(selt) {
						var timeInterval = selt.getAttribute('value');
						timeIntervalNow = timeInterval;
						createWebUrlChart(timeInterval);
					});

			var createWebUrlChart = function(timeInterval) {
				$.ajax({
							url : "getWebUrlData.do",
							data : {
								timeInterval : timeInterval
							},
							success : function(data) {
								$("#webUrlDiv").html(data.content.data);
							}
						});
			}
			createWebUrlChart(180);
			timeIntervalNow = 180;
		});