<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>	
	<div id="map" class="map" />
	
	<r:script>
	var locationLayer = L.geoJson(null, {
		style: function (feature){
			return feature.properties && feature.properties.style;
		}
	});
	var locationValueLayer = null
	var locationInfoLayer = null
	var dataLocationValueLayer = null
	var dataLocationInfoLayer = null
	
	if(${currentLocation.getLocationChildren(locationSkipLevels) != null && 
		!currentLocation.getLocationChildren(locationSkipLevels).empty}){
		locationValueLayer = L.geoJson(null, {
			pointToLayer: fctDataLocationValuePointToLayer
		});
		locationInfoLayer = L.geoJson(null, {
			pointToLayer: fctDataLocationValuePointToLayer
		});
		
		//TODO mapLocations returns an array of layers
		mapLocations(locationLayer, true, false);
	    mapLayers = [locationLayer, locationValueLayer, locationInfoLayer]
	    mapTheMap();
	}
	else{
		dataLocationValueLayer = L.geoJson(null, {
			pointToLayer: fctDataLocationValuePointToLayer
		});
		dataLocationInfoLayer = L.geoJson(null, {
			pointToLayer: fctDataLocationInfoPointToLayer
		});
		
		//TODO mapLocations returns an array of layers
		mapLocations(locationLayer, false, true);
		mapLayers = [locationLayer, dataLocationValueLayer, dataLocationInfoLayer]
		mapTheMap();
		
		var overlays = {
			//TODO message.properties
			"Facilities": dataLocationInfoLayer
		};
		L.control.layers(null, overlays).addTo(map);
	}

	function mapLocations(locationLayer, mapLocationValues, mapDataLocationValues){
		var locationUrl = null;
		if(mapDataLocationValues){
			var location = "${currentLocation.code}";
			locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][equals]="+location;
		}
		else{
			var locations = "${reportLocations.collect{it.code}.join('|')}";
			locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][in]="+locations;
		}
		jQuery.getJSON(locationUrl, function(data){
			//TODO check data != null		
			jQuery.each(data.features, function(i,f){
				var polygonCoordinates = createPolygonCoordinates(f);
				var geojsonPolygonFeature = createGeoJsonPolygonFeature(f, polygonCoordinates);
				locationLayer.addData(geojsonPolygonFeature);							
			});
			map.fitBounds(locationLayer.getBounds());
			
			if(mapLocationValues){
				//TODO map location values
			}
			if(mapDataLocationValues){
				//TODO mapLocations returns an array of layers
				mapDataLocations();
			}
		});
	}
	
	function mapDataLocations(){
		var fosaLocations = [];
		var dataLocations = "${reportLocations.collect{it.code}.join('|')}";
	    var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+dataLocations;
		jQuery.getJSON(dataLocationUrl, function(data){
			//TODO check data != null
			jQuery.each(data.features, function(i,f){	
				$('.js-map-table-value.js-selected-value[data-location-code="'+f.properties.fosaid+'"]').each(function(index, mapTableValue){					
					fosaLocations.push(f.properties.fosaid);
					var locationName = $(mapTableValue).data('location-names');						
					var indicatorName = $(mapTableValue).data('indicator-names');
					var indicatorClass = $(mapTableValue).data('indicator-class');
					var mapValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
					var rawValue = $(mapValue).data('report-value-raw');
					var reportValue = $(mapValue).data('report-value');
					var reportValueType = $(mapValue).data('report-value-type');
					var reportValueIcon = "${resource(dir:'images',file:'/maps/report-value-null.png')}";
					if(!f.geometry){
						//fosa coordinates missing
						missingFosaCoordinates(f.properties.fosaid);
					}
					else{
						//create point geojson feature
						var geojsonPointFeature = 
							createGeoJsonPointFeature(f, locationName, indicatorName, indicatorClass, rawValue, reportValue, reportValueType, reportValueIcon);
						dataLocationValueLayer.addData(geojsonPointFeature);
						dataLocationInfoLayer.addData(geojsonPointFeature);
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