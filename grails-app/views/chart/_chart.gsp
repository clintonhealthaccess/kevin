<div id="chart_div"></div>

<script type="text/javascript">
	google.load("visualization", "1", {"callback": drawChart, packages:["corechart"]});
	
	function drawChart() {
		$.ajax({
			type: 'GET',
			url: "${createLink(controller: 'chart', action: 'chart', params: [data: data, organisation: organisation])}",
			success: function(data) {
				var dataTable = new google.visualization.DataTable();
				dataTable.addColumn('string', 'Year');
				dataTable.addColumn('number', data.chart.organisation);
				dataTable.addRows(4);
				var it = 0;
				$.each(data.chart.values, function(key, value) {
					dataTable.setValue(it, 0, key);
					if (value != "null") dataTable.setValue(it, 1, value);
					it++;
				});
				
				var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
				chart.draw(dataTable, {width: 480, height: 270, vAxis:{minValue: 0}, chartArea:{left: 32, top: 16, height: 224}, backgroundColor: "#F4F4F4"});
			}
		});
		
	}
</script>