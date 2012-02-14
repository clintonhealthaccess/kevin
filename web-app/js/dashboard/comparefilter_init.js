$(document).ready(function() {
	
	var programCompareDiv = $('.horizontal-bar-avg');
	var locationCompareDiv = $('.horizontal-graph-average');	
	programCompareDiv.hide();
	locationCompareDiv.hide();
	
	$('#program-compare').bind('change', function() {
		var locationId = $(this).val();
		if(locationId == 0){
			programCompareDiv.hide();				
		} else {
			compareFilter('program', locationId);	
		}					
	});		
	$('#location-compare').bind('change', function() {
		var locationId = $(this).val();
		if(locationId == 0){
			locationCompareDiv.hide();				
		} else {
			compareFilter('location', locationId);	
		}							
	});		
			
});

function compareFilter(table, locationId) {
	$('#'+table+'-form').ajaxSubmit(function(data) {
		if(table == 'program'){
			$.each(data.compareValues, function(index, compareValue) {
				var dashboardEntityId = compareValue.id;
				var percentageValue = compareValue.value;
				var compareDiv = $('#compare-dashboard-entity-'+dashboardEntityId);
				if(percentageValue == null){
					$(compareDiv).css('width', '0%');	
					$(compareDiv).attr('title', 'N/A');
					$(compareDiv).attr('data-percentage', 'N/A');							
				}
				else{							
					$(compareDiv).css('width', percentageValue + '%');						
					$(compareDiv).attr('title', percentageValue + '%');
					$(compareDiv).attr('data-percentage', percentageValue);
					if(percentageValue > 100){ 
						$(compareDiv).addClass('expand-bar');
						$(compareDiv).css('width', '100%');
					}
				}
				$(compareDiv).show();					
			})
		}
		if(table == 'location'){				
			var percentageValue = data.compareValues[0].value;
			var compareDiv = $('.horizontal-graph-average');
			var tooltip = $(compareDiv).children('.tooltip');
			var tickmark = $(compareDiv).children('.horizontal-graph-marker');
			if(percentageValue == null){
				$(tooltip).css('left', '0%');
				$(tickmark).css('width', '0%');
				$(tooltip).attr('title', 'N/A');
				$(tooltip).attr('data-percentage', 'N/A');
				
			}
			else{						
				$(tooltip).css('left', percentageValue + '%')
				$(tickmark).css('width', percentageValue + '%');
				$(tooltip).attr('title', percentageValue + '%')
				$(tooltip).attr('data-percentage', percentageValue);
				if(percentageValue > 100){
					$(tooltip).addClass('.expand-tick');
					$(tooltip).css('left', '100%');
					$(tickmark).css('width', '100%');
					}
				}
				$(compareDiv).show();	
			}
		}
	);		
}