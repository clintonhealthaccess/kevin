var basePolygonLayer = L.geoJson(null, {
	style: function (feature){
		return feature.properties && feature.properties.style;
	},
	pointToLayer: polygonFeatureToLayer,
	onEachFeature: onEachPolygonFeature
});

var locationNameLayer = null
var locationValueNaLayer = null
var locationValueLayerMap = []
var locationLayers = []
var overlays = []

function fctMap(childrenCollectData, currentLocationCode, reportLocationCodes){
	
	var geoJsonValueLayerOptions = null;

	if(childrenCollectData){
		geoJsonValueLayerOptions = {
			pointToLayer: pointValueFeatureToLayer,
			onEachFeature: onEachPointValueFeature
		};

		var geoJsonLabelLayerOptions = {
			pointToLayer: pointLabelFeatureToLayer,
			onEachFeature: onEachPointValueFeature
		};
		locationNameLayer = L.geoJson(null, geoJsonLabelLayerOptions);
		locationLayers.push(locationNameLayer)
		overlays["Facilities"] = locationNameLayer;
	}
	else{
		geoJsonValueLayerOptions = {
			pointToLayer: polygonValueFeatureToLayer,
			onEachFeature: onEachPolygonValueFeature
		};
	}

	var mapTableIndicators = $('.js-map-table-indicator');
	$(mapTableIndicators).each(function(index, mapTableIndicator){
		var indicatorCode = $(mapTableIndicator).data('indicator-code');
		var indicatorName = $(mapTableIndicator).data('indicator-names');
		var locationValueLayer = L.geoJson(null, geoJsonValueLayerOptions);
		locationValueLayerMap[indicatorCode] = locationValueLayer;
		overlays[indicatorName] = locationValueLayer;
		locationLayers.push(locationValueLayer);
	});
	
	locationValueNaLayer = L.geoJson(null, geoJsonValueLayerOptions);
	overlays["N/A"] = locationValueNaLayer;
	locationLayers.push(locationValueNaLayer);

	mapPolygons(childrenCollectData, currentLocationCode, reportLocationCodes);

    mapLayers = locationLayers;
    createTheMap(childrenCollectData);

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
			var rawValue = $(mapReportValue).data('report-value-raw');
			if(parseFloat(rawValue)){
				indicatorRawValueMap[indicatorCode] = rawValue;
			}
			else{
				indicatorRawValueMap[indicatorCode] = -1;
			}
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

			// if fct report value is number, uncomment line below
			var reportValue = $(mapReportValue).data('report-value');

			// if fct report value is percentage, uncomment line below
			// var reportValue = $(mapPercentageValue).data('report-value');

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

			// add bubble
			var geojsonPointFeature = createGeoJsonPointFeature(feature);
			var locationValueLayer = locationValueLayerMap[indicatorCode];
			locationValueLayer.addData(geojsonPointFeature);

			// add bubble value
			feature.properties.reportValueIcon = reportValueLabelIcon;
			geojsonPointFeature = createGeoJsonPointFeature(feature);
			locationValueLayer.addData(geojsonPointFeature);
		});
							
	});
}

function polygonValueFeatureToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var polygonValueLabel = feature.properties.reportValueIcon;

	// fct raw value is always percentage
	if(rawValue == null) rawValue = 0.10

	// polygon bubble value
	if(polygonValueLabel){
		var wrapperAnchorValue = reportValue < 10 ? new L.Point(6, 10) : (reportValue < 100 ? new L.Point(12, 10) : new L.Point(18, 10));
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

	// polygon bubble
	if(rawValue != null){
		var indicatorClass = feature.properties.indicatorClass;
		var radiusValue = (rawValue*20)+15; //min: 15px max: 35px
		radiusValue = radiusValue < 15 ? 15 : (radiusValue > 35 ? 35 : radiusValue);
		var geojsonMarkerOptions = {
			radius: radiusValue,
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
			fosaLocations.push(fosaid+"");

			var mapTableValues = $('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]');

			if(mapTableValues.size() == 0){
				mapTableValues = $('.js-map-table-value[data-location-code="'+fosaid+'"]').first();
			}

			$(mapTableValues).each(function(index, mapTableValue){

				var mapValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
				var rawValue = $(mapValue).data('report-value-raw');
				var reportValue = $(mapValue).data('report-value');

				var indicatorCode = rawValue != null ? $(mapTableValue).data('indicator-code') : null;

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
							"indicatorClass": rawValue !=null ? $(mapTableValue).data('indicator-class') : null,
							"indicatorCode": rawValue !=null ? $(mapTableValue).data('indicator-code') : null,
							"indicatorName": rawValue !=null ? $(mapTableValue).data('indicator-names') : null,
							"rawValue": rawValue,
							"reportValue": reportValue,
							"reportValueIcon": reportValueLabelIcon
						}
					};

					// add value
					var geojsonPointFeature = createGeoJsonPointFeature(feature);
					if(rawValue != null){
						var locationValueLayer = locationValueLayerMap[indicatorCode];
						locationValueLayer.addData(geojsonPointFeature);
					}
					else locationValueNaLayer.addData(geojsonPointFeature);

					// add value label
					locationNameLayer.addData(geojsonPointFeature);
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

	// if rawValue != null, create a circle marker, else create an 'na' marker
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
		var pointValue = new L.Icon.Label.Default({					
			iconUrl: pointValueLabel,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: reportValue+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(13, 5),
			labelClassName: 'report-value-marker-na',
			shadowUrl: null
		});
		var geojsonMarkerOptions = {icon: pointValue};
		var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
	   	return geojsonMarker;
	}
}

function pointLabelFeatureToLayer(feature, latlng) {
	
	var locationName = feature.properties.locationName;
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var pointValueLabel = feature.properties.reportValueIcon;

	var pointValue = new L.Icon.Label.Default({					
		iconUrl: pointValueLabel,
		iconSize: new L.Point(20, 20),
		hideIcon: true,
		labelText: locationName+'',
		labelAnchor: new L.Point(0, 0),
		// if rawValue != null, position the label to the right of value, else position the label above the value
		wrapperAnchor: rawValue != null ? new L.Point(-12, 7) : new L.Point(13, 15),
		labelClassName: 'report-value-marker-point-label',
		shadowUrl: null
	});
	var geojsonMarkerOptions = {icon: pointValue};
	var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
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