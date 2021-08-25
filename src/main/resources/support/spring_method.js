Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
var timeIntervalNow;
var createSpringMethodDetailChart = function(className, method) {
	$('#myModal').modal({
				backdrop : false
			})
	$.ajax({
				url : "getSpringMethodDetailData.do",
				data : {
					className : className,
					method : method,
					timeInterval : timeIntervalNow
				},
				success : function(data) {
					var count = [];
					var concurrentMax = [];
					for (var i = 0; i < data.content.data.length; i++) {
						count.push([data.content.data[i].timeStamp,
								data.content.data[i].springMethodStat.Count]);
						concurrentMax
								.push([
										data.content.data[i].timeStamp,
										data.content.data[i].springMethodStat.ConcurrentMax]);

					}

					var yLabelFormatter = function() {
						return this.value;
					}

					var param = {
						title : className,
						renderTo : 'springMethodDetailDiv',
						yLabelFormatter : yLabelFormatter,
						series : [{
									name : '访问次数',
									data : count
								}, {
									name : '最大并发',
									data : concurrentMax
								}]
					};
					var options = buildOption(param);
					new Highcharts.Chart(options);
				}
			});

	$.ajax({
				url : "getSpringMethodErrorDetailData.do",
				data : {
					className : className,
					method : method,
					timeInterval : timeIntervalNow
				},
				success : function(data) {
					$("#springMethodErrorDiv").html(data.content.data);
				}
			});

}

$(document).ready(function() {

			// 绑定tab切换事件
			changeTab('timeChangeDiv', function(selt) {
						var timeInterval = selt.getAttribute('value');
						timeIntervalNow = timeInterval;
						createSpringMethodChart(timeInterval);
					});

			var createSpringMethodChart = function(timeInterval) {
				$.ajax({
							url : "getSpringMethodForHtml.do",
							data : {
								timeInterval : timeInterval
							},
							success : function(data) {
								$("#springMethodDiv").html(data.content.data);
							}
						});
			}
			createSpringMethodChart(180);
			timeIntervalNow = 180;
		});