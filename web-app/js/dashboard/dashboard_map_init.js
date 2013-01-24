var basePolygonLayer = L.geoJson(null, {
	style: function (feature){
		return feature.properties && feature.properties.style;
	},
	pointToLayer: dashboardPolygonFeatureToLayer,
	onEachFeature: onEachPolygonFeature
});

var locationValueLayer = null;
var locationValueLayers = []

function dashboardMap(childrenCollectData, currentLocationCode, reportLocationCodes){
	
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

	var mapTableIndicator = $('.js-map-table-value[data-location-code="'+currentLocationCode+'"]');
	var indicatorName = $(mapTableIndicator).data('indicator-names');
	locationValueLayer = L.geoJson(null, geoJsonLayerOptions);
	locationValueLayers.push(locationValueLayer);
	
	mapDashboardPolygons(childrenCollectData, currentLocationCode, reportLocationCodes);

    mapLayers = locationValueLayers;
    createTheMap();

	// alert("after everything ");
}

// dashboard base polygon layer
// polygon -> (color = report value)
// polygon label -> location name

function mapDashboardPolygons(childrenCollectData, currentLocationCode, reportLocationCodes){
	
	var locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][in]=";
	if(childrenCollectData) locationUrl += currentLocationCode; // map 1 polygon
	else locationUrl += reportLocationCodes; // map 1+ polygons

	jQuery.getJSON(locationUrl, function(data){
	
		// TODO
		if(data == null){
			return;
		}
		
		jQuery.each(data.features, function(index, dataFeature){

			var fosaid = dataFeature.properties.code;
			var mapTableLocation = $('.js-map-table-location[data-location-code="'+fosaid+'"]');
			var locationName = $(mapTableLocation).data('location-names');

			var mapTableValue = $('.js-map-table-value[data-location-code="'+fosaid+'"]');
			var mapReportValue = $(mapTableValue).children('div.report-value');
			var rawValue = $(mapReportValue).data('report-value-raw');
			var reportValue = $(mapReportValue).data('report-value');

			var polygonFillColor = null;
			var polygonColor = null;
			if(rawValue != null){
				var quartile = rawValue < 0.26 ? 0 : (rawValue < 0.51 ? 1 : (rawValue < 0.76 ? 2 : (rawValue < 1.01 ? 3 : 4)));
				quartile = 'indicator-quartile-'+quartile
				polygonFillColor = mapPolygonColors[quartile]
				polygonColor = mapPolygonColorsDark[quartile]
			
			}
			else{
				quartile = 'indicator-quartile-na'
				polygonFillColor = mapPolygonColors[quartile]
				polygonColor = mapPolygonColorsDark[quartile]
			}

			var polygonStyle = {
				color: polygonColor,
				weight: 1.5,
				fillColor: polygonFillColor,
			    fillOpacity: 0.75
			};

			var polygonCoordinates = createPolygonCoordinates(dataFeature, false);

			// add polygon
			var polygonFeature = {
				"id": fosaid,
				"geometry": {
			    	"coordinates": polygonCoordinates
				},
				"properties":{
					"locationCode": fosaid,
					"locationName": locationName,
					"style": polygonStyle
				}
			};
			var geojsonPolygonFeature = createGeoJsonPolygonFeature(polygonFeature);
			basePolygonLayer.addData(geojsonPolygonFeature);
			
			// add polygon label if children are not facilities
			if(!childrenCollectData){
				var center = L.multiPolygon(polygonCoordinates).getBounds().getCenter();
				var multiPolygonCenter = [center.lat, center.lng];

				var polygonLabelFeature = {
					"id": fosaid,
					"type": "Feature",
				    "geometry": {
				    	"coordinates": multiPolygonCenter
					},
					"properties":{
						"locationCode": fosaid,
						"locationName": locationName,
						"reportValueIcon": reportValueLabelIcon
					}
				};
				var geojsonPointFeature = createGeoJsonPointFeature(polygonLabelFeature);
				basePolygonLayer.addData(geojsonPointFeature);
			}
							
		});

		if(childrenCollectData) 
			mapPointValues(reportLocationCodes);
		else mapPolygonValues(data);

		mapTheMap(!childrenCollectData);
	});
	// })
	// .success(function() { alert("second success"); })
	// .error(function() { alert("error"); })
	// .complete(function() { alert("complete"); });
}

function dashboardPolygonFeatureToLayer(feature, latlng) {

	var polygonLabel = feature.properties.reportValueIcon;

	// polygon label
	if(polygonLabel){
		var locationName = feature.properties.locationName;
		polygonLabel = new L.Icon.Label.Default({					
			iconUrl: polygonLabel,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: locationName+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(-10, 10),
			labelClassName: 'report-value-marker-polygon-label',
			shadowUrl: null
		});
		var geojsonMarkerOptions = {icon: polygonLabel};
		var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
	   	return geojsonMarker;
	}
}

// dashboard polygon value layer
// polygon value -> (font size = report value)

function mapPolygonValues(data){
	jQuery.each(data.features, function(i,dataFeature){
		
		var fosaid = dataFeature.properties.code;

		// map 1 point label per location
		var mapTableValue = $('.js-map-table-value[data-location-code="'+fosaid+'"]');
		var mapReportValue = $(mapTableValue).children('div.report-value');
		var rawValue = $(mapReportValue).data('report-value-raw');
		var reportValue = $(mapReportValue).data('report-value');
		var reportValueType = $(mapReportValue).data('report-value-type');

		var polygonCoordinates = createPolygonCoordinates(dataFeature, false);
		var bounds = L.multiPolygon(polygonCoordinates).getBounds();
		var center = bounds.getCenter();

		// position each point label per indicator
		var latLng = null;
		//TODO figure out why this returns NorthWest bounds
		var northWest = bounds.getSouthEast();
		latLng = createNorthSouthOffset(northWest.lng, center);

		var feature = {
			"id": fosaid,
		    "geometry": {
		        "coordinates": latLng
			},
			"properties":{
				"locationCode": $(mapTableValue).attr('data-location-code'),
				"locationName": $(mapTableValue).data('location-names'),
				"indicatorCode": $(mapTableValue).data('indicator-code'),
				"indicatorName": $(mapTableValue).data('indicator-names'),
				"rawValue": rawValue,
				"reportValue": reportValue,
				"reportValueType": reportValueType,
				"reportValueIcon": reportValueLabelIcon
			}
		};

		// add point label
		if(reportValueType == 'NUMBER'){
			rawValue = parseFloat(rawValue);				
			var maxRawValue = getMaxRawValue();
			var percentageMaxRawValue = parseFloat(rawValue/maxRawValue);
			var reportValueSize = parseInt(percentageMaxRawValue*20)+10; //min: 10px max: 30px
		}
		feature.properties.reportValueSize = reportValueSize;
		var geojsonPointFeature = createGeoJsonPointFeature(feature);
		locationValueLayer.addData(geojsonPointFeature);
							
	});
}

function polygonValueFeatureToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var polygonValueLabel = feature.properties.reportValueIcon;
	
	var rawValueFontSize = null;
	var labelClassName = null;
	
	if(rawValue != null){
		polygonValueLabel = new L.Icon.Label.Default({					
				iconUrl: polygonValueLabel,
				iconSize: new L.Point(20, 20),
				hideIcon: true,
				labelText: reportValue+'',
				labelAnchor: new L.Point(0, 0),
				wrapperAnchor: new L.Point(13, 5),
				// labelClassName: '',
				shadowUrl: null
		});
		
		var reportValueSize = feature.properties.reportValueSize;
		polygonValueLabel.options.labelFontSize = reportValueSize + 'px'
		polygonValueLabel.options.labelClassName += 'report-value-marker-number'
	}
	else{
		polygonValueLabel = new L.Icon.Label.Default({					
			iconUrl: polygonValueLabel,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: reportValue+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(13, 5),
			labelClassName: rawValue == null ? 'report-value-marker-na' : 'report-value-marker-label',
			shadowUrl: null
		});
	}

	var geojsonMarkerOptions = {icon: polygonValueLabel};
	var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
}

// dashboard polygon value layer interactions

function onEachPolygonValueFeature(feature, layer) {
	layer.on({
        mouseover: highlightPolygonValueFeature,
        mouseout: resetPolygonValueFeature,
        // click: zoomToFeature
   	});
}

function highlightPolygonValueFeature(e) {
	//TODO mimic highlight polygon feature to prevent 'unhilight' when hovering over polygon value

    var target = e.target;
    var rawValue = target.feature.properties.rawValue
    var reportValueType = target.feature.properties.reportValueType
	var indicatorCode = target.feature.properties.indicatorCode
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(reportValueType){
    		// highlight map marker number, the only map marker without a constant font size
    		if(reportValueType == 'NUMBER'){
    			var fontSize = parseFloat(target.feature.properties.reportValueSize);
    			var valueLabel = $(target._icon).children('.report-value-marker-number');
    			$(valueLabel).css('font-size', fontSize+2);
    		} 
    		// highlight map table cell
	    	highlightMapTableValue(locationCode, indicatorCode);	    	   		
    	}
    	else{
    		// highlight map table row
	    	highlightMapTableLocation(locationCode);	
    	}
    }
    // highlight map table cell
    highlightMapTableValue(locationCode, indicatorCode);
}

function resetPolygonValueFeature(e) {
	var target = e.target;
	var rawValue = target.feature.properties.rawValue
	var reportValueType = target.feature.properties.reportValueType
	var indicatorCode = target.feature.properties.indicatorCode
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(reportValueType){
    		// reset map marker, the only map marker without a constant font size
    		if(reportValueType == 'NUMBER'){
    			var fontSize = parseFloat(target.feature.properties.reportValueSize);
    			var valueLabel = $(target._icon).children('.report-value-marker-number');
    			$(valueLabel).css('font-size', fontSize);
    		}
    		// reset map table cell
    		resetMapTableValue(locationCode, indicatorCode);    		
    	}
    	else{
    		// reset map table row
    		resetMapTableLocation(locationCode);
    	}
    }
}

// dashboard point value layer 
// point value -> font size = report value)
// point value label -> location name

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

			// map 1 point & point label per location
			var mapTableValues = $('.js-map-table-value[data-location-code="'+fosaid+'"]');

			$(mapTableValues).each(function(index, mapTableValue){

				var mapValue = $(mapTableValue).children('div.report-value');
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
							"rawValue": $(mapValue).data('report-value-raw'),
							"reportValue": $(mapValue).data('report-value')
						}
					};

					// add point
					var rawValue = $(mapValue).data('report-value-raw');
					var reportValueType = $(mapValue).data('report-value-type');
					feature.properties.reportValueType = $(mapValue).data('report-value-type');					
					if(reportValueType == 'NUMBER'){
						rawValue = parseFloat(rawValue);				
						var maxRawValue = getMaxRawValue();
						var percentageMaxRawValue = parseFloat(rawValue/maxRawValue);
						var reportValueSize = parseInt(percentageMaxRawValue*20)+10; //min: 10px max: 30px
						reportValueSize = reportValueSize > 30 ? 30 : (reportValueSize < 10 ? 10 : reportValueSize);
					}
					feature.properties.reportValueSize = reportValueSize;
					var geojsonPointFeature = createGeoJsonPointFeature(feature);
					locationValueLayer.addData(geojsonPointFeature);

					// add point label
					feature.properties.reportValueIcon = reportValueLabelIcon;
					var geojsonPointFeature = createGeoJsonPointFeature(feature);
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
	var reportValueType = feature.properties.reportValueType;
	var pointValueLabel = feature.properties.reportValueIcon;

	//point value label
	if(pointValueLabel){
			pointValueLabel = new L.Icon.Label.Default({					
			iconUrl: pointValueLabel,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: locationName+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(13, 15),
			labelClassName: 'report-value-marker-point-label',
			shadowUrl: null
		});
	}
	// point value
	else{
		if(rawValue != null){
			pointValueLabel = new L.Icon.Label.Default({					
					iconUrl: reportValueLabelIcon,
					iconSize: new L.Point(20, 20),
					hideIcon: true,
					labelText: reportValue+'',
					labelAnchor: new L.Point(0, 0),
					wrapperAnchor: new L.Point(13, 5),
					// labelClassName: '',
					shadowUrl: null
			});
			
			var reportValueSize = feature.properties.reportValueSize;
			pointValueLabel.options.labelFontSize = reportValueSize + 'px'
			pointValueLabel.options.labelClassName += 'report-value-marker-number'
		}
		else{
			pointValueLabel = new L.Icon.Label.Default({					
				iconUrl: reportValueLabelIcon,
				iconSize: new L.Point(20, 20),
				hideIcon: true,
				labelText: reportValue+'',
				labelAnchor: new L.Point(0, 0),
				wrapperAnchor: new L.Point(13, 5),
				labelClassName: rawValue == null ? 'report-value-marker-na' : 'report-value-marker-label',
				shadowUrl: null
			});
		}
	}

	var geojsonMarkerOptions = {icon: pointValueLabel};
	var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
}

// dashboard point value layer interactions

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
    var reportValueType = target.feature.properties.reportValueType
	var indicatorCode = target.feature.properties.indicatorCode
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(reportValueType){
    		// highlight map marker number, the only map marker without a constant font size
    		if(reportValueType == 'NUMBER'){
    			var fontSize = parseFloat(target.feature.properties.reportValueSize);
    			var valueLabel = $(target._icon).children('.report-value-marker-number');
    			$(valueLabel).css('font-size', fontSize+2);
    		} 
    		// highlight map table cell
	    	highlightMapTableValue(locationCode, indicatorCode);	    	   		
    	}
    	else{
    		// highlight map table row
	    	highlightMapTableLocation(locationCode);	
    	}
    }
}

function resetPointValueFeature(e) {
	var target = e.target;
	var rawValue = target.feature.properties.rawValue
	var reportValueType = target.feature.properties.reportValueType
	var indicatorCode = target.feature.properties.indicatorCode
    var locationCode = target.feature.properties.locationCode
    if(rawValue != null){
    	if(reportValueType){
    		// reset map marker, the only map marker without a constant font size
    		if(reportValueType == 'NUMBER'){
    			var fontSize = parseFloat(target.feature.properties.reportValueSize);
    			var valueLabel = $(target._icon).children('.report-value-marker-number');
    			$(valueLabel).css('font-size', fontSize);
    		}
    		// reset map table cell
    		resetMapTableValue(locationCode, indicatorCode);    		
    	}
    	else{
    		// reset map table row
    		resetMapTableLocation(locationCode);
    	}
    }
}