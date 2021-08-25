Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
var timeIntervalNow;
var createWebIPDetailChart = function(ip) {

	$('#myModal').modal({
				backdrop : false
			})
	$.ajax({
				url : "getIPDetailData.do",
				data : {
					ip : ip,
					timeInterval : timeIntervalNow
				},
				success : function(data) {
					var count = [];
					var concurrentMax = [];
					for (var i = 0; i < data.content.data.length; i++) {
						count.push([data.content.data[i].timeStamp,
								data.content.data[i].webIPStat.Count]);
						concurrentMax.push([data.content.data[i].timeStamp,
								data.content.data[i].webIPStat.ConcurrentMax]);

					}

					var yLabelFormatter = function() {
						return this.value;
					}

					var urlParam = {
						title : ip,
						renderTo : 'ipDetailDiv',
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
				url : "getIPErrorDetailData.do",
				data : {
					ip : ip
				},
				success : function(data) {
					$("#ipErrorDiv").html(data.content.data);
				}
			});

}

$(document).ready(function() {

			// 绑定tab切换事件
			changeTab('timeChangeDiv', function(selt) {
						var timeInterval = selt.getAttribute('value');
						timeIntervalNow = timeInterval;
						createWebIPChart(timeInterval);
					});

			var createWebIPChart = function(timeInterval) {
				$.ajax({
							url : "getWebIPData.do",
							data : {
								timeInterval : timeInterval
							},
							success : function(data) {
								$("#webIPDiv").html(data.content.data);
							}
						});
			}
			createWebIPChart(180);
			timeIntervalNow = 180;
		});