<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>
	<g:if test="${mapSkipLevels != null && mapSkipLevels.contains(currentLocation.level)}">
		<p class='nodata'>
			<g:message code="dsr.report.map.selectdistrict.label" />
		</p>
	</g:if>
	<div id="map" class="map" />
	
	<r:script>
	var baseLocationLayer = null
	var dataLocationValueLayer = null
	var dataLocationInfoLayer = null
	
	var overlays = []
	
	if(${mapSkipLevels != null && !mapSkipLevels.contains(currentLocation.level)}){
		baseLocationLayer = L.geoJson(null, {
			style: function (feature){
				return feature.properties && feature.properties.style;
			}
		});
		dataLocationValueLayer = L.geoJson(null, {pointToLayer: dsrDataLocationValuePointToLayer});
		dataLocationInfoLayer = L.geoJson(null, {pointToLayer: dsrDataLocationInfoPointToLayer});
		
		mapLocations();
		mapLayers = [baseLocationLayer, dataLocationValueLayer, dataLocationInfoLayer];
	}
	
	createTheMap();
	overlays["Facilities"] = dataLocationInfoLayer;
	L.control.layers(null, overlays).addTo(map);
	
	function mapLocations(){
		var locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][equals]=${currentLocation.code}";
		jQuery.getJSON(locationUrl, function(data){
		
			//alert("success");
			//TODO
			if(data == null){
				return;
			}
			
			jQuery.each(data.features, function(i,f){
				var polygonCoordinates = createPolygonCoordinates(f, false);
				var geoJsonPolygonFeature = createGeoJsonPolygonFeature(f, polygonCoordinates);
				baseLocationLayer.addData(geoJsonPolygonFeature);
			});
			mapTheMap(baseLocationLayer, false, true);
			mapDataLocations();
		});
		//TODO
		//.success(function() { alert("second success"); })
		//.error(function() { alert("error"); })
		//.complete(function() { alert("complete"); });
	}
	
	function mapDataLocations(){
		var fosaLocations = [];
		var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]=${reportLocations.collect{it.code}.join('|')}";
		jQuery.getJSON(dataLocationUrl, function(data){
			
			//alert("success");
			//TODO
			if(data == null){
				return;
			}
			
			jQuery.each(data.features, function(i,f){
				var fosaid = f.properties.fosaid;
				$('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]').each(function(index, mapTableValue){
					var mapValue = $(mapTableValue).children('div.report-value');
					fosaLocations.push(fosaid+"");					
					
					if(!f.geometry){
						//fosa coordinates missing
						missingFosaCoordinates(f.properties.fosaid);
					}
					else {
					
						var feature = {
							"id": fosaid,
						    "geometry": {
						        "coordinates": f.geometry.coordinates
							},
							"properties":{
								"locationName": $(mapTableValue).data('location-names'),
								"indicatorName": $(mapTableValue).data('indicator-names'),
								//"indicatorClass": $(mapTableValue).data('indicator-class'),
								//TODO use report table for raw value, report value, and report value type
								"rawValue": $(mapValue).data('report-value-raw'),
								"reportValue": $(mapValue).data('report-value'),
								"reportValueType": $(mapValue).data('report-value-type'),
								"reportValueIcon": "${resource(dir:'images',file:'/maps/report-value-null.png')}"
							}
						};
						//create point geojson feature
						var geojsonPointFeature = createGeoJsonPointFeature(feature);
						dataLocationValueLayer.addData(geojsonPointFeature);
						dataLocationInfoLayer.addData(geojsonPointFeature);
					}				
				});
			});
			//fosa locations missing
			var dhsstLocations = ("${reportLocations.collect{it.code}.join(';')}").split(';');
			missingFosaLocations(fosaLocations, dhsstLocations);									
		});
		//TODO
		//.success(function() { alert("second success"); })
		//.error(function() { alert("error"); })
		//.complete(function() { alert("complete"); });
	}
	</r:script>
</div>