$(document).ready(function() {
			$.ajax({
						url : "getJDBCInfoData.do",
						data : {},
						success : function(data) {
							$("#jdbcInfoDiv").html(data.content.data);
						}
					});
		});