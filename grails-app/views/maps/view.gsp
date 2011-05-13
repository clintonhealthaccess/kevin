<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="cost.view.label" default="Costing" /></title>
        
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
    </head>
    <body>
    	<div id="maps">
			<div id="top" class="box">
				test
			</div>
    		<div id="center" class="box">
    			<div id="map_canvas"></div>
    		</div>
    	</div>
    	
    	<script type="text/javascript">
    		var map;
    	
			function initialize() {
				var latlng = new google.maps.LatLng(-1.93,29.84);
				var myOptions = {
					zoom: 9,
					center: latlng,
					mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				
				map = new google.maps.Map(document.getElementById("map_canvas"),myOptions);
  			}
    	
    		function drawMap() {
    			$.ajax({
    				type: 'GET',
    				url: "${createLink(controller: 'maps', action: 'map')}",
    				data: {},
    				success: function(data) {
    					if (data.result == 'success') {
    						$.each(data.map.polygons, function(key, element){
	    						var polygon = [];
	    						$.each(element.coordinates[0][0], function(key, element){
									polygon.push(new google.maps.LatLng(element[1], element[0]))
	    						});
	    						
								var organisation = new google.maps.Polygon({
									paths: polygon,
									strokeColor: "#FF0000",
									strokeOpacity: 0.8,
									strokeWeight: 2,
									fillColor: "#FF0000",
									fillOpacity: 0.35
								});
									
								organisation.setMap(map);
								google.maps.event.addListener(organisation, 'mouseover', function(){
									alert(element.name);
								});
							})
    					}
    				},
    				error: function(data, textStatus, error) {}
    			});
    		}
    	
			jQuery(document).ready(function() {
				initialize();
				
				// TEST
				drawMap();
			});
    	</script>
    </body>
</html>