<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>
	<g:if test="${mapSkipLevels != null && mapSkipLevels.contains(currentLocation.level)}">
		<p class='nodata'>
			<g:message code="dsr.report.map.selectdistrict.label" />
		</p>
	</g:if>
	<div id="map" class="map" />
	
	<r:script>
	if(${mapSkipLevels != null && 
		!mapSkipLevels.contains(currentLocation.level)}){
		var locationLayer = L.geoJson(null, {
			style: function (feature){
				return feature.properties && feature.properties.style;
			}
		});
		var dataLocationValueLayer = L.geoJson(null, {pointToLayer: dsrDataLocationValuePointToLayer});
		
		//TODO mapLocations returns an array of layers
		mapLocations();
		mapLayers = [locationLayer, dataLocationValueLayer];
	}
	
	mapTheMap();
	
	function mapLocations(){
		var locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][equals]=${currentLocation.code}";
		jQuery.getJSON(locationUrl, function(data){
			//TODO check data != null
			jQuery.each(data.features, function(i,f){
				var polygonCoordinates = createPolygonCoordinates(f);
				var geoJsonPolygonFeature = createGeoJsonPolygonFeature(f, polygonCoordinates);
				locationLayer.addData(geoJsonPolygonFeature);
			});
			map.fitBounds(locationLayer.getBounds());
			mapDataLocations();
		});
	}
	
	function mapDataLocations(){
		var fosaLocations = [];
		var fosaIds = "${reportLocations.collect{it.code}.join('|')}";
		var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+fosaIds;
		jQuery.getJSON(dataLocationUrl, function(data){
			//TODO check data != null
			jQuery.each(data.features, function(i,f){
				$('.js-map-table-value.js-selected-value[data-location-code="'+f.properties.fosaid+'"]').each(function(index, mapLocation){
					fosaLocations.push(f.properties.fosaid);
					var locationName = $(mapLocation).data('location-names');						
					var indicatorName = $(mapLocation).data('indicator-names');
					var mapValue = $(mapLocation).children('div.report-value');
					var rawValue = $(mapValue).data('report-value-raw');			
					var reportValue = $(mapValue).data('report-value');
					var reportValueType = $(mapValue).data('report-value-type');
					var reportValueIcon = "${resource(dir:'images',file:'/maps/report-value-null.png')}";
					if(!f.geometry){
						//fosa coordinates missing
						missingFosaCoordinates(f.properties.fosaid);
					}
					else {
						//create geojson point feature
						var geojsonPointFeature = 
							createGeoJsonPointFeature(f, locationName, indicatorName, null, rawValue, reportValue, reportValueType, reportValueIcon);
						dataLocationValueLayer.addData(geojsonPointFeature);
					}				
				});
			});
			//fosa locations missing
			var dhsstLocations = ${reportLocations.collect{it.code}};
			missingFosaLocations(fosaLocations, dhsstLocations);									
		});
	}
	</r:script>
</div>