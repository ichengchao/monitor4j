
// 切换tab的效果以及回调
var changeTab = function(id, callback) {
	var oNav = document.getElementById(id);
	var oA = oNav.getElementsByTagName('a');
	for (var i = 0; i < oA.length; i++) {
		oA[i].onclick = function() {
			for (var i = 0; i < oA.length; i++) {
				oA[i].className = '';
			}
			this.className = "active";
			callback(this);
		};
	}
};

var buildOption = function(param) {
	var options = {
		title : {
			style : {
				fontWeight : 'bold',
				fontSize : '12px',
				fontFamily : 'Microsoft yahei',
				color : '#333'
			},
			text : param.title
		},
		chart : {
			renderTo : param.renderTo,
			type : 'line',
			zoomType : 'x'
		},
		// xAxis : {
		// categories : categories
		// },
		xAxis : {
			type : 'datetime',
			// tickInterval : 480 * 1000, // one week
			tickPixelInterval : 300,
			// categories : [1, 2, null, 5],
			title : {
				text : null
			},
			dateTimeLabelFormats : {
				second : '%m-%e %H:%M:%S',
				minute : '%m-%e %H:%M:%S',
				hour : '%m-%e %H:%M',
				day : '%m-%e %H:%M',
				week : '%m-%e %H:%M',
				month : '%m-%e %H:%M',
				year : '%Y'
			}
		},
		yAxis : {
			title : {
				text : null
			},
			min : 0,
			labels : {
				formatter : param.yLabelFormatter
			}

		},
		tooltip : {
			shared : true,
			crosshairs : true
		},
		// 隐藏右下角logo
		credits : {
			enabled : true,
			text : 'powered by jmonitor',
			href : 'http://jmonitor.cn'
		},
		series : param.series,

		plotOptions : {
			series : {
				tooltip : {
					dateTimeLabelFormats : {
						second : '%m-%e %H:%M:%S',
						minute : '%m-%e %H:%M:%S',
						hour : '%m-%e %H:%M',
						day : '%m-%e %H:%M',
						week : '%m-%e %H:%M',
						month : '%m-%e %H:%M',
						year : '%Y'
					}
				}
			},
			line : {
				lineWidth : 2,
				states : {
					hover : {
						lineWidth : 2
					}
				},
				// 去掉点的显示
				marker : {
					enabled : false
				}
			}
		}
	};

	return options;
};
