<div class='map-wrap'>
	<g:if test="${viewSkipLevels.contains(currentLocation.level)}">
		<p class='nodata'>
			<g:message code="dsr.report.map.noinformation.exceptdistrict.label" />
		</p>
	</g:if>
	<div id="map" style="width: 968px; height: 500px"></div>
	<!-- TODO move this to a map_init.js file -->
	<r:script>
		var map = L.map('map').setView([-1.951069, lng=30.06134], 9);
		L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
			maxZoom: 18,
			//TODO move this to message.properties?
			attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
					'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
					'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
		}).addTo(map);
				
		<!-- Locations -->
		var locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][equals]=${currentLocation.code}";		
		jQuery.getJSON(locationUrl, function(data){
			jQuery.each(data.features, function(i,f){
						
				//create polygon coordinates
				var polygonCoordinates = []
				var coordinates = []
				var latlonRegex = /\[(\-|\d|\.)*,(\-|\d|\.)*\]/g;
				$(f.properties.coordinates.match(latlonRegex)).each(function(){
					var coordinate = this;
					coordinate = this.replace(/(\[|\])/g,"");
					var lat = parseFloat(coordinate.split(',')[0]);
					var lon = parseFloat(coordinate.split(',')[1]);
					coordinates.push([lat, lon]);					
				});
				polygonCoordinates.push(coordinates);
				
				//create polygon geojson feature
				var geojsonPolygonFeature = {
						"id": f.properties.code,
					    "type": f.type,    
					    "geometry": {
					        "type": f.properties.featuretype,
					        "coordinates": polygonCoordinates
					    },
					    "properties": {
					    	//TODO use i to get hex code from a color map to set color styles
					        "style": {
								color: "#7FCDBB", //"#99D8C9", //"#7FCDBB", //"#258CD5",
					        	weight: 4,
					        	fillColor: "#7FCDBB", //"#99D8C9", //"#7FCDBB", //"#258CD5",
					            fillOpacity: 0.5 //0.4 //0.4
					        }
					    }
				};
				var geojsonPolygonLayer = L.geoJson(geojsonPolygonFeature, {style: geojsonPolygonFeature.properties.style}).addTo(map);
				map.fitBounds(geojsonPolygonLayer.getBounds());														
			});
		});
		
		<!-- Data Locations -->
		var dataLocations = []
		var maxReportValue = 0;
		$('div.js-map-location[data-target-code="${currentTarget.code}"]').each(function(){
	        var dataLocation = $(this).data('location-code');
	        dataLocations.push(dataLocation);	        
	        var valueType = $(this).children('div.report-value').data('report-value-type');
	        var value = $(this).children('div.report-value').data('report-value')
	        if(valueType == 'NUMBER' && parseFloat(value) > maxReportValue){
				maxReportValue = parseFloat(value);
			}			
	    });
	    var fosaIds = dataLocations.join('|');
		
		var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+fosaIds;
		jQuery.getJSON(dataLocationUrl, function(data){
			
			var geojsonPointLayer = L.geoJson(null, {pointToLayer: dataLocationPointToLayer, onEachFeature: onEachFeature}).addTo(map);			
			
			jQuery.each(data.features, function(i,f){			
				
				var location = $('div.js-map-location[data-location-code="'+f.properties.fosaid+'"][data-target-code="${currentTarget.code}"]');			
				var names = $(location).data('location-names');
				var reportValue = $(location).children('div.report-value');			
				var value = $(reportValue).data('report-value');
				var valueType = $(reportValue).data('report-value-type');				
				
				//create point geojson feature
				var geojsonPointFeature = {
						"id": f.properties.fosaid,
					    "type": f.type,    
					    "geometry": {
					        "type": f.geometry.type,
					        "coordinates": f.geometry.coordinates
					    },
					    "properties": {				    	
					    	"name": names,
					    	"reportValue": value,
					    	"reportValueType": valueType,
					        "popupContent": names+'<br />'+"${i18n(field: currentTarget.names)}"+': '+value
					    }
				};
				geojsonPointLayer.addData(geojsonPointFeature);
			});									
		});
		
		function dataLocationPointToLayer(feature, latlng) {
			
			var reportValue = feature.properties.reportValue;
			var reportValueType = feature.properties.reportValueType;				
			
			var reportValueIcon = null;			
			var geojsonMarkerOptions = null;
			var geojsonMarker = null;
			
			switch(reportValueType){
				//TODO use ValueType enum
				case 'BOOL':
					if(reportValue){
						reportValueIcon = L.icon({
							iconUrl: "${resource(dir:'images',file:'/maps/report-value-true.png')}",
							iconSize: [20, 20]
						});
					}
					else{
						reportValueIcon = L.icon({
							iconUrl: "${resource(dir:'images',file:'/maps/report-value-false.png')}",
							iconSize: [20, 20]
						});
					}
					geojsonMarkerOptions = {icon: reportValueIcon};						
        			geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
					break;
				case 'NUMBER':
					var numSize = parseFloat(reportValue/maxReportValue)*25;
					reportValueIcon = new L.HtmlIcon({
					    html : "<span style='color:#fff;font-size:"+numSize+"px;font-weight:bold;'>"+reportValue+"</span>"
					});
					geojsonMarkerOptions = {icon: reportValueIcon};		
        			geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
					break;
				default:
					reportValueIcon = L.icon({
							iconUrl: "${resource(dir:'images',file:'/maps/report-value-null.png')}",
							iconSize: [20, 20]
					});
					geojsonMarkerOptions = {icon: reportValueIcon};		
        			geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
					break;
			}
        	return geojsonMarker;		
    	}
		
		function onEachFeature(feature, layer) {
		    if (feature.properties && feature.properties.popupContent) {
		        layer.bindPopup(feature.properties.popupContent);
	    	}
		}			
	</r:script>
</div>