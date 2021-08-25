Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
var timeIntervalNow;

var showLogInfoDetail = function(exceptionType, methodName) {
	$('#myModal').modal({
				backdrop : false
			})
	$.ajax({
				url : "getLogInfoDetailData.do",
				data : {
					exceptionType : exceptionType,
					methodName : methodName,
					timeInterval : timeIntervalNow
				},
				success : function(data) {
					var count = [];
					for (var i = 0; i < data.content.data.length; i++) {
						count.push([data.content.data[i].timeStamp,
								data.content.data[i].count]);
					}

					var yLabelFormatter = function() {
						return this.value;
					}

					var logInfoParam = {
						title : exceptionType + "<br>(" + methodName + ")",
						renderTo : 'logInfoDetailDiv',
						yLabelFormatter : yLabelFormatter,
						series : [{
									name : '异常总数',
									data : count
								}]
					};
					var logInfoOptions = buildOption(logInfoParam);

					logInfoOptions.plotOptions.series.events = {
						click : function(event) {
							var timestamp = event.point.x;
							$.ajax({
										url : "getLogInfoStrackTraceData.do",
										data : {
											exceptionType : exceptionType,
											methodName : methodName,
											timestamp : timestamp
										},
										success : function(data) {
											$("#logInfoStackTraceDiv")
													.html(data.content.data);
										}
									});
						}
					};
					new Highcharts.Chart(logInfoOptions);
				}
			});
}

$(document).ready(function() {

			// 绑定tab切换事件
			changeTab('timeChangeDiv', function(selt) {
						var timeInterval = selt.getAttribute('value');
						timeIntervalNow = timeInterval;
						createLogInfoChart(timeInterval);
					});

			var createLogInfoChart = function(timeInterval) {
				$.ajax({
							url : "getLogInfoData.do",
							data : {
								timeInterval : timeInterval
							},
							success : function(data) {
								$("#logInfoDiv").html(data.content.data);
							}
						});
			}
			createLogInfoChart(180);
			timeIntervalNow = 180;
		});