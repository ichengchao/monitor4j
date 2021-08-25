Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
var showStackTrace = function(threadId, timestamp) {
	$.ajax({
				url : "getThreadInfoStackData.do",
				data : {
					threadId : threadId,
					timestamp : timestamp
				},
				success : function(data) {
					$("#threadInfoStackDiv").html(data.content.data);
					// console.log(data.content.data);
				}
			});
}

$(document).ready(function() {

	// 绑定tab切换事件
	changeTab('timeChangeDiv', function(selt) {
				var timeInterval = selt.getAttribute('value');
				timeIntervalNow = timeInterval;
				createThreadChart(timeInterval);
			});

	var createThreadChart = function(timeInterval) {
		$.ajax({
					url : "getThreadInfoData.do",
					data : {
						timeInterval : timeInterval
					},
					success : function(data) {
						var threadCount = [];
						var daemonThreadCount = [];
						var deadLockedThreadCount = [];
						for (var i = 0; i < data.content.data.length; i++) {
							threadCount.push([data.content.data[i].timeStamp,
									data.content.data[i].ThreadCount]);
							daemonThreadCount.push([
									data.content.data[i].timeStamp,
									data.content.data[i].DaemonThreadCount]);
							deadLockedThreadCount
									.push([
											data.content.data[i].timeStamp,
											data.content.data[i].DeadLockedThreadCount]);

						}

						var yLabelFormatter = function() {
							return this.value;
						}

						var threadParam = {
							title : '线程',
							renderTo : 'threadInfoDiv',
							yLabelFormatter : yLabelFormatter,
							series : [{
										name : '总线程',
										data : threadCount
									}, {
										name : 'daemon线程',
										data : daemonThreadCount
									}, {
										name : '死锁线程',
										data : deadLockedThreadCount
									}]
						};
						var threadOptions = buildOption(threadParam);
						// $("#threadInfoDetailDiv").html(data.content.data);
						new Highcharts.Chart(threadOptions);
					}
				});
	}
	createThreadChart(180);
});