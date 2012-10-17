<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>	
	<div id="map" class="map" />
	
	<r:script>
	var baseLocationLayer = L.geoJson(null, {
		style: function (feature){
			return feature.properties && feature.properties.style;
		},
		onEachFeature: onEachBaseLocationLayerFeature
	});
	var baseLocationInfoLayer = L.geoJson(null, {
		pointToLayer: fctBaseLocationInfoPointToLayer
	});
	
	var locationValueLayers = []
	var locationValueLayerMap = []
	var locationInfoLayers = []
	var locationInfoLayerMap = []
	
	//TODO data location value and info layer map
	var dataLocationValueLayer = null
	var dataLocationInfoLayer = null
	
	var overlays = []
	
	if(${currentLocation.getLocationChildren(locationSkipLevels) != null && 
		!currentLocation.getLocationChildren(locationSkipLevels).empty}){
		
		var mapTableIndicators = $('.js-map-table-indicator');
		$(mapTableIndicators).each(function(index, mapTableIndicator){
			var indicatorName = $(mapTableIndicator).data('indicator-names');
			var locationValueLayer = L.geoJson(null, {
				pointToLayer: fctLocationValuePointToLayer
			});
			var locationInfoLayer = L.geoJson(null, {
				pointToLayer: fctLocationInfoPointToLayer
			});
			locationValueLayerMap[indicatorName] = locationValueLayer;
			locationInfoLayerMap[indicatorName] = locationInfoLayer;
			overlays[indicatorName] = L.layerGroup([locationValueLayer, locationInfoLayer]);
			
			locationValueLayers.push(locationValueLayer);
			locationInfoLayers.push(locationInfoLayer);
		});
		
		mapLocations(baseLocationLayer, true, false);
	    mapLayers = ([baseLocationLayer, baseLocationInfoLayer]).concat(locationValueLayers).concat(locationInfoLayers);
	    createTheMap();
	     //TODO message.properties
	    overlays["Locations"] = baseLocationInfoLayer;
	    L.control.layers(null, overlays).addTo(map);
	}
	else{
		dataLocationValueLayer = L.geoJson(null, {
			pointToLayer: fctDataLocationValuePointToLayer
		});
		dataLocationInfoLayer = L.geoJson(null, {
			pointToLayer: fctDataLocationInfoPointToLayer
		});
		
		mapLocations(baseLocationLayer, false, true);
		mapLayers = ([baseLocationLayer, baseLocationInfoLayer]).concat([dataLocationValueLayer, dataLocationInfoLayer]);
		createTheMap();
		 //TODO message.properties
		overlays["Facilities"] = dataLocationInfoLayer;
		L.control.layers(null, overlays).addTo(map);
	}

	//alert("after everything ");

	function mapLocations(baseLocationLayer, mapLocationValueLayer, mapDataLocationValueLayer){
		var locationUrl = null;
		if(mapDataLocationValueLayer){
			locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][equals]=${currentLocation.code}";
		}
		else{
			locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][in]=${reportLocations.collect{it.code}.join('|')}";
		}
		jQuery.getJSON(locationUrl, function(data){
		
			//alert("success");
			//TODO
			if(data == null){
				return;
			}
			
			jQuery.each(data.features, function(i,f){
				var polygonCoordinates = createPolygonCoordinates(f, false);
				var geojsonPolygonFeature = createGeoJsonPolygonFeature(f, polygonCoordinates);
				baseLocationLayer.addData(geojsonPolygonFeature);
				
				if(mapLocationValueLayer){
					var multiPolygon = L.multiPolygon(polygonCoordinates);
					var bounds = multiPolygon.getBounds();
					var center = bounds.getCenter();
					var multiPolygonCoordinates = [center.lat, center.lng];
					
					var fosaid = f.properties.code;
					var mapTableValue = $('.js-map-table-value[data-location-code="'+fosaid+'"]').first();
					var feature = {
						"id": fosaid,
						"type": "Feature",
					    "geometry": {
					    	"type": "Point",
					        "coordinates": multiPolygonCoordinates
						},
						"properties":{
							"locationName": $(mapTableValue).data('location-names'),
							"reportValueIcon": "${resource(dir:'images',file:'/maps/report-value-null.png')}"
						}
					};
					baseLocationInfoLayer.addData(feature);
				}
								
			});
			
			mapTheMap(baseLocationLayer, mapLocationValueLayer, mapDataLocationValueLayer);
			
			if(mapLocationValueLayer){
				mapLocationValues(data);
			}
			if(mapDataLocationValueLayer){
				mapDataLocations();
			}
		});
		//TODO
		//.success(function() { alert("second success"); })
		//.error(function() { alert("error"); })
		//.complete(function() { alert("complete"); });
	}
	
	function mapLocationValues(data){
		jQuery.each(data.features, function(i,f){
			
			var fosaid = f.properties.code;
			
			var indicatorRawValueMap = [];
			var mapTableValues = $('.js-map-table-value[data-location-code="'+fosaid+'"]');
			$(mapTableValues).each(function(index, mapTableValue){
				var indicatorName = $(mapTableValue).data('indicator-names');
				var mapReportValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
				var rawValue = parseFloat($(mapReportValue).data('report-value-raw'));
				indicatorRawValueMap[indicatorName] = rawValue;
			});
			var sortedIndicators = sortMapByValue(indicatorRawValueMap);
			
			$(sortedIndicators).each(function(index, sortedIndicator){
				
				var mapTableIndicator = $('.js-map-table-indicator[data-indicator-names="'+sortedIndicator+'"]');
				var indicatorName = $(mapTableIndicator).data('indicator-names');
				var indicatorClass = $(mapTableIndicator).data('indicator-class');
				
				var latLngPolygonCoordinates = []
				var latLngCoordinates = []
				var polygonCoordinates = createPolygonCoordinates(f, false);
				var multiPolygon = L.multiPolygon(polygonCoordinates);
				var bounds = multiPolygon.getBounds();
				var center = bounds.getCenter();
				var multiPolygonCoordinates = [center.lat, center.lng];
				
				if(sortedIndicators.length > 1){
					if(indicatorClass == "indicator-best"){
						//TODO figure out why this returns NorthWest bounds
						var northWest = bounds.getSouthEast();
						multiPolygonCoordinates = createNorthSouthOffsetCoordinates(northWest, center);
					}
					else if(indicatorClass == "indicator-middle"){
						var southWest = bounds.getSouthWest();
						multiPolygonCoordinates = createEastWestOffsetCoordinates(southWest, center);
					}
					else if(indicatorClass == "indicator-worst"){
						//TODO figure out why this returns SouthEast bounds
						var southEast = bounds.getNorthWest();
						multiPolygonCoordinates = createNorthSouthOffsetCoordinates(southEast, center);
					}
				}
				
				var mapTableValue = $('.js-map-table-value[data-indicator-names="'+indicatorName+'"][data-location-code="'+fosaid+'"]');
				var mapReportValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
				var mapPercentageValue = $(mapTableValue).children('div.report-value-percentage').children('div.report-value');
				
				var feature = {
					"id": fosaid,
				    "geometry": {
				        "coordinates": multiPolygonCoordinates
					},
					"properties":{
						"indicatorClass": $(mapTableValue).data('indicator-class')
					}
				};
				
				feature.properties.rawValue = $(mapPercentageValue).data('report-value-raw');
				feature.properties.reportValue = $(mapPercentageValue).data('report-value');
				
				//create point geojson feature
				var geojsonPointFeature = createGeoJsonPointFeature(feature);			
				var locationValueLayer = locationValueLayerMap[indicatorName];
				locationValueLayer.addData(geojsonPointFeature);

				feature.properties.rawValue = $(mapReportValue).data('report-value-raw');
				feature.properties.reportValue = $(mapReportValue).data('report-value');
				feature.properties.reportValueIcon = "${resource(dir:'images',file:'/maps/report-value-null.png')}"
				
				//create point geojson feature
				geojsonPointFeature = createGeoJsonPointFeature(feature);
				var locationInfoLayer = locationInfoLayerMap[indicatorName];
				locationInfoLayer.addData(geojsonPointFeature);
			});
								
		});
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
					var mapValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');					
					fosaLocations.push(fosaid+"");
					if(!f.geometry){
						//fosa coordinates missing
						missingFosaCoordinates(f.properties.fosaid);
					}
					else{
						var feature = {
							"id": fosaid,
						    "geometry": {
						        "coordinates": f.geometry.coordinates
							},
							"properties":{
								"locationName": $(mapTableValue).data('location-names'),
								"indicatorName": $(mapTableValue).data('indicator-names'),
								"indicatorClass": $(mapTableValue).data('indicator-class'),
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