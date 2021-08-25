$(document).ready(function() {
			$.ajax({
						url : "getJvmInfoData.do",
						data : {},
						success : function(data) {
							$("#jvmInfoDiv").html(data.content.data);
						}
					});
		});