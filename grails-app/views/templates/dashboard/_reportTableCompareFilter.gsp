<div class="selector right">
	<span>Compare</span> 
	<select id="${table}-compare">
		<option>Please select</option>
		<g:each in="${dashboard.organisationPath}" var="location">
			<option value="${location.id}">
				<g:i18n field="${location.names}" />
			</option>
		</g:each>
		<option value="${currentOrganisation.id}">
			<g:i18n field="${currentOrganisation.names}" />
		</option>
	</select>
</div>

<script type="text/javascript">
$(document).ready(function() {
	$('.horizontal-bar-avg.tooltip').hide();
 	$('#program-compare').bind('change', function() {
		var locationId = $(this).val();
		if(locationId > 0){
			compareFilter('program', locationId);
		}
		else {
			$('.horizontal-bar-avg.tooltip').hide();
		}
		return;
	});
 	$('.horizontal-graph-average').hide();
	$('#location-compare').bind('change', function() {
		var locationId = $(this).val();
		if(locationId > 0){
			compareFilter('location', locationId);
		}
		else {
			$('.horizontal-graph-average').hide();
		}
		return;
	});
	
	function compareFilter(table, locationId) {
		var data = new Array();		
		data['organisation'] = locationId;
		$.ajax({
			type: 'GET',
			data: data,
			url: "${createLink(controller:'dashboard', action:'compare', params:[period:currentPeriod.id, objective:currentObjective.id, dashboardEntity: dashboardEntity.id])}",
			success: function(data) {
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
					var dashboardEntityId = $('.horizontal-graph-average').data('entity');
					var percentageValue = data.compareValues[dashboardEntityId].value;
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
			},
			error: function() {
				alert('error, dude!');
			}
		});
	}		
});	
</script>