Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});

$(document).ready(function() {

	// 绑定tab切换事件
	changeTab('timeChangeDiv', function(selt) {
				var timeInterval = selt.getAttribute('value');
				createHeapChart(timeInterval);
				createGCChart(timeInterval);
				createThreadChart(timeInterval);
			});

	var createHeapChart = function(timeInterval) {
		$.ajax({
			url : "getMemoryData.do",
			data : {
				timeInterval : timeInterval
			},
			success : function(data) {
				var useHeapDataArr = [];
				var maxHeapDataArr = [];
				var useNoHeapDataArr = [];
				var maxNoHeapDataArr = [];

				var usePermGenDataArr = [];
				var maxPermGenDataArr = [];
				var useOldGenDataArr = [];
				var maxOldGenDataArr = [];
				var useEdenSpaceDataArr = [];
				var maxEdenSpaceDataArr = [];
				var useSurvivorDataArr = [];
				var maxSurvivorDataArr = [];

				for (var i = 0; i < data.content.data.length; i++) {
					// heap
					useHeapDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].HeapMemoryUsed]);
					maxHeapDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].HeapMemoryMax]);
					// non-heap
					useNoHeapDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].NonHeapMemoryUsed]);
					maxNoHeapDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].NonHeapMemoryMax]);
					// PermGen
					usePermGenDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].PermGenUsed]);
					maxPermGenDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].PermGenMax]);
					// OldGen
					useOldGenDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].OldGenUsed]);
					maxOldGenDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].OldGenMax]);
					// EdenSpace
					useEdenSpaceDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].EdenSpaceUsed]);
					maxEdenSpaceDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].EdenSpaceMax]);
					// Survivor
					useSurvivorDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].SurvivorUsed]);
					maxSurvivorDataArr.push([data.content.data[i].timeStamp,
							data.content.data[i].SurvivorMax]);

				}

				var yLabelFormatter = function() {
					return this.value / (1000 * 1000) + 'MB';
				}

				var heapParam = {
					title : '堆内存',
					renderTo : 'heapMemoryDiv',
					yLabelFormatter : yLabelFormatter,
					series : [{
								name : 'max',
								data : maxHeapDataArr
							}, {
								name : 'used',
								data : useHeapDataArr
							}]
				};

				var noHeapParam = {
					title : '非堆内存',
					renderTo : 'noHeapMemoryDiv',
					yLabelFormatter : yLabelFormatter,
					series : [{
								name : 'max',
								data : maxNoHeapDataArr
							}, {
								name : 'used',
								data : useNoHeapDataArr
							}]
				};

				var otherMemoryParam = {
					title : '内存详细信息',
					renderTo : 'otherMemoryDiv',
					yLabelFormatter : yLabelFormatter,
					series : [{
								name : 'PermGen max',
								data : maxPermGenDataArr
							}, {
								name : 'PermGen used',
								data : usePermGenDataArr
							}, {
								name : 'OldGen max',
								data : maxOldGenDataArr
							}, {
								name : 'OldGen used',
								data : useOldGenDataArr
							}, {
								name : 'EdenSpace max',
								data : maxEdenSpaceDataArr
							}, {
								name : 'EdenSpace used',
								data : useEdenSpaceDataArr
							}, {
								name : 'Survivor max',
								data : maxSurvivorDataArr
							}, {
								name : 'Survivor used',
								data : useSurvivorDataArr
							}]
				};

				var heapOptions = buildOption(heapParam);
				var noHeapOptions = buildOption(noHeapParam);
				var otherMemoryOptions = buildOption(otherMemoryParam);

				new Highcharts.Chart(heapOptions);
				new Highcharts.Chart(noHeapOptions);
				var otherMemoryChart = new Highcharts.Chart(otherMemoryOptions);
				otherMemoryChart.series[0].hide();
				otherMemoryChart.series[2].hide();
				otherMemoryChart.series[4].hide();
				otherMemoryChart.series[6].hide();
			}
		});
	}

	var createGCChart = function(timeInterval) {
		$.ajax({
					url : "getGCData.do",
					data : {
						timeInterval : timeInterval
					},
					success : function(data) {
						var fullGCCountDataArr = [];
						var youngGCCountDataArr = [];
						var fullGCTimeDataArr = [];
						var youngGCTimeDataArr = [];
						for (var i = 0; i < data.content.data.length; i++) {
							fullGCCountDataArr
									.push([
											data.content.data[i].timeStamp,
											data.content.data[i].SpanFullGCCollectionCount]);
							youngGCCountDataArr
									.push([
											data.content.data[i].timeStamp,
											data.content.data[i].SpanYoungGCCollectionCount]);
							fullGCTimeDataArr
									.push([
											data.content.data[i].timeStamp,
											data.content.data[i].SpanFullGCCollectionTime]);
							youngGCTimeDataArr
									.push([
											data.content.data[i].timeStamp,
											data.content.data[i].SpanYoungGCCollectionTime]);
						}

						var yLabelFormatter = function() {
							return this.value;
						}

						var countParam = {
							title : 'GC 次数',
							renderTo : 'gcCountDiv',
							yLabelFormatter : yLabelFormatter,
							series : [{
										name : 'fullGC',
										data : fullGCCountDataArr
									}, {
										name : 'youngGC',
										data : youngGCCountDataArr
									}]
						};

						var timeParam = {
							title : 'GC 时间',
							renderTo : 'gcTimeDiv',
							yLabelFormatter : yLabelFormatter,
							series : [{
										name : 'fullGC(ms)',
										data : fullGCTimeDataArr
									}, {
										name : 'youngGC(ms)',
										data : youngGCTimeDataArr
									}]
						};

						var countChart = new Highcharts.Chart(buildOption(countParam));
						var timeChart = new Highcharts.Chart(buildOption(timeParam));
					}
				});
	}

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
						var processCpuTimeRate = []
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
							processCpuTimeRate.push([
									data.content.data[i].timeStamp,
									data.content.data[i].ProcessCpuTimeRate]);

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

						var cpuRateParam = {
							title : 'CPU百分比',
							renderTo : 'cpuRateDiv',
							yLabelFormatter : yLabelFormatter,
							series : [{
										name : '百分比',
										data : processCpuTimeRate
									}]
						};

						var threadOptions = buildOption(threadParam);
						var cpuRateOptions = buildOption(cpuRateParam);
						new Highcharts.Chart(threadOptions);
						new Highcharts.Chart(cpuRateOptions);
					}
				});
	}

	createHeapChart(180);
	createGCChart(180);
	createThreadChart(180);

});