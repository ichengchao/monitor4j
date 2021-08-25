Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});
var timeIntervalNow;
var createJdbcSqlDetailChart = function(sql) {
	alert("todo:" + sql);
}
$(document).ready(function() {

			// 绑定tab切换事件
			changeTab('timeChangeDiv', function(selt) {
						var timeInterval = selt.getAttribute('value');
						timeIntervalNow = timeInterval;
						createSqlChart(timeInterval);
					});

			var createSqlChart = function(timeInterval) {
				$.ajax({
							url : "getJdbcSqlData.do",
							data : {
								timeInterval : timeInterval
							},
							success : function(data) {
								$("#jdbcSqlDiv").html(data.content.data);
							}
						});
			}
			createSqlChart(180);
			timeIntervalNow = 180;
		});