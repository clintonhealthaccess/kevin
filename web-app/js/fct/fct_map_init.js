var basePolygonLayer = L.geoJson(null, {
	style: function (feature){
		return feature.properties && feature.properties.style;
	},
	pointToLayer: polygonFeatureToLayer,
	onEachFeature: onEachPolygonFeature
});

var locationValueLayerMap = []
var locationValueLayers = []
var overlays = []

function fctMap(childrenCollectData, currentLocationCode, reportLocationCodes){
	
	var geoJsonLayerOptions = null;

	if(childrenCollectData){
		geoJsonLayerOptions = {
			pointToLayer: pointValueFeatureToLayer,
			onEachFeature: onEachPointValueFeature
		};
	}
	else{
		geoJsonLayerOptions = {
			pointToLayer: polygonValueFeatureToLayer,
			onEachFeature: onEachPolygonValueFeature
		};
	}

	var mapTableIndicators = $('.js-map-table-indicator');
	$(mapTableIndicators).each(function(index, mapTableIndicator){
		var indicatorCode = $(mapTableIndicator).data('indicator-code');
		var indicatorName = $(mapTableIndicator).data('indicator-names');
		var locationValueLayer = L.geoJson(null, geoJsonLayerOptions);
		locationValueLayerMap[indicatorCode] = locationValueLayer;
		overlays[indicatorName] = locationValueLayer;
		locationValueLayers.push(locationValueLayer);
	});
	
	mapPolygons(childrenCollectData, currentLocationCode, reportLocationCodes);
    mapLayers = locationValueLayers;
    createTheMap();
    L.control.layers(null, overlays).addTo(map);

	// alert("after everything ");
}

// fct polygon value layer 
// polygon value -> combination of (color = indicator) x (radius size = raw value)
// polygon value label -> report value
// indicator -> best = green, middle = yellow, worst = red

function mapPolygonValues(data){
	jQuery.each(data.features, function(i,dataFeature){
		
		var fosaid = dataFeature.properties.code;
		
		// sort indicators by descending report value
		var sortedIndicators = null;
		var indicatorRawValueMap = [];
		var mapTableValues = $('.js-map-table-value[data-location-code="'+fosaid+'"]');
		$(mapTableValues).each(function(index, mapTableValue){
			var indicatorCode = $(mapTableValue).data('indicator-code');
			var mapReportValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
			var rawValue = parseFloat($(mapReportValue).data('report-value-raw'));
			indicatorRawValueMap[indicatorCode] = rawValue;
		});
		sortedIndicators = sortMapByValue(indicatorRawValueMap);

		// map 1 value & 1 value label for each indicator for the polygon
		$(sortedIndicators).each(function(index, sortedIndicator){
			
			var mapTableIndicator = $('.js-map-table-indicator[data-indicator-code="'+sortedIndicator+'"]');
			var indicatorClass = $(mapTableIndicator).data('indicator-class');
			var indicatorCode = $(mapTableIndicator).data('indicator-code');
			var indicatorName = $(mapTableIndicator).data('indicator-names');
			
			var polygonCoordinates = createPolygonCoordinates(dataFeature, false);
			var bounds = L.multiPolygon(polygonCoordinates).getBounds();
			var center = bounds.getCenter();

			var mapTableValue = $('.js-map-table-value[data-indicator-names="'+indicatorName+'"][data-location-code="'+fosaid+'"]');
			var mapReportValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
			var mapPercentageValue = $(mapTableValue).children('div.report-value-percentage').children('div.report-value');
			var rawValue = $(mapPercentageValue).data('report-value-raw');
			var reportValue = $(mapPercentageValue).data('report-value');

			var latLng = null;
			if(sortedIndicators.length > 1){
				if(indicatorClass == "indicator-best"){
					//TODO figure out why this returns NorthWest bounds
					var northWest = bounds.getSouthEast();
					latLng = createNorthSouthOffset(northWest.lng, center);
				}
				else if(indicatorClass == "indicator-middle"){
					//TODO figure out why this *doesn't* return NorthEast bounds :P
					var southWest = bounds.getSouthWest();
					latLng = createEastWestOffset(southWest.lat, center);
				}
				else if(indicatorClass == "indicator-worst"){
					//TODO figure out why this returns SouthEast bounds
					var southEast = bounds.getNorthWest();
					latLng = createNorthSouthOffset(southEast.lng, center);
				}
			}

			var feature = {
				"id": fosaid,
			    "geometry": {
			        "coordinates": latLng
				},
				"properties":{
					"locationCode": $(mapTableValue).attr('data-location-code'),
					"locationName": $(mapTableValue).data('location-names'),
					"indicatorClass": indicatorClass,
					"indicatorCode": indicatorCode,
					"indicatorName": indicatorName,
					"rawValue": rawValue,
					"reportValue": reportValue
				}
			};

			// if value != null, add value
			if(rawValue != null){
				var geojsonPointFeature = createGeoJsonPointFeature(feature);			
				var locationValueLayer = locationValueLayerMap[indicatorCode];
				locationValueLayer.addData(geojsonPointFeature);
			}

			// always add value label
			feature.properties.reportValueIcon = reportValueLabelIcon;
			var geojsonPointFeature = createGeoJsonPointFeature(feature);
			var locationValueLayer = locationValueLayerMap[indicatorCode];
			locationValueLayer.addData(geojsonPointFeature);
		});
							
	});
}

function polygonValueFeatureToLayer(feature, latlng) {
	
	var indicatorClass = feature.properties.indicatorClass;
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var polygonValueLabel = feature.properties.reportValueIcon;

	// polygon value label
	if(polygonValueLabel){
		
		// if fct report value is number, uncomment line below
		// var wrapperAnchorValue = rawValue < 10 ? new L.Point(6, 10) : (rawValue < 100 ? new L.Point(11, 10) : new L.Point(17, 10));

		// if fct report value is percentage, we need extra room for the '%', uncomment line below
		var wrapperAnchorValue = rawValue < 0.1 ? new L.Point(14, 10) : (rawValue < 1 ? new L.Point(19, 10) : (rawValue < 10 ? new L.Point(24, 10) : new L.Point(29, 10)));

		var reportValueIcon = feature.properties.reportValueIcon;
		reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: reportValueIcon,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: reportValue+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: wrapperAnchorValue,
			labelClassName: rawValue == null ? 'report-value-marker-na' : 'report-value-marker-label',
			shadowUrl: null
		});
		var geojsonMarkerOptions = {icon: reportValueIcon};
		var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
	   	return geojsonMarker;
	}

	// polygon value
	if(rawValue != null){

		// if fct raw value is number, uncomment lines below
		// var radiusValue = rawValue
		// radiusValue = radiusValue < 10 ? 10 : (radiusValue > 35 ? 35 : radiusValue);

		// if fct raw value is percentage, we need extra room for the '%', uncomment lines below
		var radiusValue = parseInt(rawValue*25)+10;
		radiusValue = radiusValue < 15 ? 15 : (radiusValue > 35 ? 35 : radiusValue);
		radiusValue = rawValue < 1 ? radiusValue*1.33 : (rawValue < 10 ? radiusValue*1.66 : radiusValue*2);

		var geojsonMarkerOptions = {
			radius: rawValue == 0 ? 0 : radiusValue,
		    fillColor: mapMarkerColors[indicatorClass],
		    color: mapMarkerColors[indicatorClass],
		    weight: 1,
		    opacity: 1,
		    fillOpacity: 0.75
		};
		var geojsonMarker = L.circleMarker(latlng, geojsonMarkerOptions);
		return geojsonMarker;
	}
}

// fct polygon value layer interactions

function onEachPolygonValueFeature(feature, layer) {
	layer.on({
        mouseover: highlightPolygonValueFeature,
        mouseout: resetPolygonValueFeature,
        // click: zoomToFeature
   	});
}

function highlightPolygonValueFeature(e) {
    var target = e.target;
    var indicatorCode = target.feature.properties.indicatorCode
    var indicatorClass = target.feature.properties.indicatorClass
    var rawValue = target.feature.properties.rawValue
    var polygonValueLabel = target.feature.properties.reportValueIcon
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(polygonValueLabel){
    		locationValueLayerMap[indicatorCode].eachLayer(function (layerData) {
				var fRawValue = layerData.feature.properties.rawValue;
				var fPolygonValueLabel = layerData.feature.properties.reportValueIcon
				var fLocationCode = layerData.feature.properties.locationCode;
				if(fRawValue == rawValue && fLocationCode == locationCode && !fPolygonValueLabel){
					// highlight map point
					layerData.setStyle({
				    	fillColor: mapMarkerDarkerColors[indicatorClass],
					    color: mapMarkerDarkerColors[indicatorClass],
					    weight: 4,
					    fillOpacity: 1
				    });
				}
			});
    	}
    	else{
    		// highlight map point
    		target.setStyle({
		    	fillColor: mapMarkerDarkerColors[indicatorClass],
			    color: mapMarkerDarkerColors[indicatorClass],
			    weight: 4,
			    fillOpacity: 1
		    });    		
    	}
    }
    // highlight map table cell
    highlightMapTableValue(locationCode, indicatorCode);
}

function resetPolygonValueFeature(e) {
	var target = e.target;
	var indicatorCode = target.feature.properties.indicatorCode
    var indicatorClass = target.feature.properties.indicatorClass
    var rawValue = target.feature.properties.rawValue
    var polygonValueLabel = target.feature.properties.reportValueIcon
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(polygonValueLabel){
    		locationValueLayerMap[indicatorCode].eachLayer(function (layerData) {
				var fRawValue = layerData.feature.properties.rawValue;
				var fPolygonValueLabel = layerData.feature.properties.reportValueIcon
				var fLocationCode = layerData.feature.properties.locationCode;
				if(fRawValue == rawValue && fLocationCode == locationCode && !fPolygonValueLabel){
					// reset map point
					layerData.setStyle({
						fillColor: mapMarkerColors[indicatorClass],
					    weight: 0,
					    fillOpacity: 0.75
					});
				}
			});
    	}
    	else{
    		// reset map point
    		target.setStyle({
				fillColor: mapMarkerColors[indicatorClass],
			    weight: 0,
			    fillOpacity: 0.75
			});
    	}
    }
    // reset map table cell
    resetMapTableValue(locationCode, indicatorCode);
}

// fct point value layer 
// point value -> combination of (color = indicator) x (radius size = constant 10)
// point value label -> location name
// indicator -> best = green, middle = yellow, worst = red

function mapPointValues(reportLocationCodes){
	var fosaLocations = [];
    var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+reportLocationCodes;
	jQuery.getJSON(dataLocationUrl, function(data){
	
		// TODO
		if(data == null){
			return;
		}
		
		jQuery.each(data.features, function(i,f){

			var fosaid = f.properties.fosaid;

			var mapTableValues = $('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]');

			$(mapTableValues).each(function(index, mapTableValue){

				var indicatorCode = $(mapTableValue).data('indicator-code')
				var mapValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
				var rawValue = $(mapValue).data('report-value-raw');
				var reportValue = $(mapValue).data('report-value');

				fosaLocations.push(fosaid+"");

				if(!f.geometry){
					// fosa coordinates missing
					missingFosaCoordinates(f.properties.fosaid);
				}
				else{
					var feature = {
						"id": fosaid,
					    "geometry": {
					        "coordinates": f.geometry.coordinates
						},
						"properties":{
							"locationCode": $(mapTableValue).attr('data-location-code'),
							"locationName": $(mapTableValue).data('location-names'),
							"indicatorClass": $(mapTableValue).data('indicator-class'),
							"indicatorCode": $(mapTableValue).data('indicator-code'),
							"indicatorName": $(mapTableValue).data('indicator-names'),
							"rawValue": rawValue,
							"reportValue": reportValue
						}
					};

					// if value != null, add value
					if(rawValue != null){
						var geojsonPointFeature = createGeoJsonPointFeature(feature);			
						var locationValueLayer = locationValueLayerMap[indicatorCode];
						locationValueLayer.addData(geojsonPointFeature);
					}

					// always add value label
					feature.properties.reportValueIcon = reportValueLabelIcon;
					var geojsonPointFeature = createGeoJsonPointFeature(feature);
					var locationValueLayer = locationValueLayerMap[indicatorCode];
					locationValueLayer.addData(geojsonPointFeature);
				}
			});								
		});						
		// fosa locations missing
		var dhsstLocations = (reportLocationCodes).split('|');
		missingFosaLocations(fosaLocations, dhsstLocations);									
	});
	// })
	// .success(function() { alert("second success"); })
	// .error(function() { alert("error"); })
	// .complete(function() { alert("complete"); });
}

function pointValueFeatureToLayer(feature, latlng) {
	
	var locationName = feature.properties.locationName;
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var pointValueLabel = feature.properties.reportValueIcon;

	// point value label
	if(pointValueLabel){
		pointValueLabel = new L.Icon.Label.Default({					
			iconUrl: pointValueLabel,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: locationName+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(-12, 7),
			labelClassName: 'report-value-marker-point-label',
			shadowUrl: null
		});
		var geojsonMarkerOptions = {icon: pointValueLabel};
		var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
	   	return geojsonMarker;
	}
	// point value "bubble"
	else{
		if(rawValue != null){
			var indicatorClass = feature.properties.indicatorClass;
	
			var geojsonMarkerOptions = {
			    radius: 10,
			    fillColor: mapMarkerColors[indicatorClass],
			    color: mapMarkerColors[indicatorClass],
			    weight: 1,
			    opacity: 1,
			    fillOpacity: 0.75
			};
			var geojsonMarker = L.circleMarker(latlng, geojsonMarkerOptions);
		   	return geojsonMarker;	
		}
		else{
			pointValueLabel = new L.Icon.Label.Default({					
				iconUrl: pointValueLabel,
				iconSize: new L.Point(20, 20),
				hideIcon: true,
				labelText: reportValue+'',
				labelAnchor: new L.Point(0, 0),
				wrapperAnchor: wrapperAnchorValue,
				labelClassName: rawValue == null ? 'report-value-marker-na' : 'report-value-marker-label',
				shadowUrl: null
			});
			var geojsonMarkerOptions = {icon: pointValueLabel};
			var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
		   	return geojsonMarker;
		}
	}
}

// fct point value layer interactions

function onEachPointValueFeature(feature, layer) {
	layer.on({
        mouseover: highlightPointValueFeature,
        mouseout: resetPointValueFeature,
        // click: zoomToFeature
   	});
}

function highlightPointValueFeature(e) {
    var target = e.target;
    var rawValue = target.feature.properties.rawValue
    var reportValueLabel = target.feature.properties.reportValueIcon
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(reportValueLabel){
    		// highlight map table row
    		highlightMapTableLocation(locationCode);
    	}
    	else{
    		// highlight map point
    		highlightPolygonValueFeature(e);  		
    	}
    }
}

function resetPointValueFeature(e) {
	var target = e.target;
    var rawValue = target.feature.properties.rawValue
    var reportValueLabel = target.feature.properties.reportValueIcon
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(reportValueLabel){
    		// reset map table row
    		resetMapTableLocation(locationCode);
    	}
    	else{
    		// reset map point
    		resetPolygonValueFeature(e);
    	}
    }
}