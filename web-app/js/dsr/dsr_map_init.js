var locationValueLayer = null;
var locationValueLayers = []

function dsrMap(childrenCollectData, currentIndicatorIsCalculation, currentLocationCode, reportLocationCodes){
	
	if(childrenCollectData || currentIndicatorIsCalculation){
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

		var mapTableIndicator = $('.js-map-table-indicator.js-selected-indicator');
		var indicatorName = $(mapTableIndicator).data('indicator-names');
		locationValueLayer = L.geoJson(null, geoJsonLayerOptions);
		locationValueLayers.push(locationValueLayer);
		
		mapPolygons(childrenCollectData, currentLocationCode, reportLocationCodes);
	}

    mapLayers = locationValueLayers;
    createTheMap();

	// alert("after everything ");
}

// dsr polygon value layer
// polygon value -> (symbol = report value type) x (font size = report value)

function mapPolygonValues(data){
	jQuery.each(data.features, function(i,dataFeature){
		
		var fosaid = dataFeature.properties.code;

		// map 1 point label per location
		var mapTableValue = $('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]');
		var indicatorCode = $(mapTableValue).data('indicator-code');
		var indicatorName = $(mapTableValue).data('indicator-names');

		var polygonCoordinates = createPolygonCoordinates(dataFeature, false);
		var bounds = L.multiPolygon(polygonCoordinates).getBounds();
		var center = bounds.getCenter();

		var mapReportValue = $(mapTableValue).children('div.report-value');
		var rawValue = $(mapReportValue).data('report-value-raw');
		var reportValue = $(mapReportValue).data('report-value');
		var reportValueType = $(mapReportValue).data('report-value-type');

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
				"indicatorCode": indicatorCode,
				"indicatorName": indicatorName,
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
	var reportValueType = feature.properties.reportValueType;
	var polygonValueLabel = feature.properties.reportValueIcon;
	
	var rawValueFontSize = null;
	var labelClassName = null;
	
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
	
	switch(reportValueType){			
		case 'BOOL':
			if(rawValue)
				polygonValueLabel.options.labelClassName += 'report-value-marker-true'
			else
				polygonValueLabel.options.labelClassName += 'report-value-marker-false'
			break;
		case 'ENUM':
			polygonValueLabel.options.labelClassName += 'report-value-marker-enum'
			break;				
		case 'STRING':
			polygonValueLabel.options.labelClassName += 'report-value-marker-string'
			break;				
		case 'TEXT':
			polygonValueLabel.options.labelClassName += 'report-value-marker-text'
			break;		
		case 'NUMBER':
			var reportValueSize = feature.properties.reportValueSize;
			polygonValueLabel.options.labelFontSize = reportValueSize + 'px'
			polygonValueLabel.options.labelClassName += 'report-value-marker-number'
			break;	
		default:
			polygonValueLabel.options.labelClassName += 'report-value-marker-na'
			break;
	}
	
	var geojsonMarkerOptions = {icon: polygonValueLabel};
	var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;		
}

// dsr polygon value layer interactions

function onEachPolygonValueFeature(feature, layer) {
	layer.on({
        mouseover: highlightPolygonValueFeature,
        mouseout: resetPolygonValueFeature,
        // click: zoomToFeature
   	});
}

function highlightPolygonValueFeature(e) {
    var target = e.target;
    var indicatorCode = target.feature.properties.indicatorCode;
    var locationCode = target.feature.properties.locationCode
    // highlight map table cell
    highlightMapTableValue(locationCode, indicatorCode);
}

function resetPolygonValueFeature(e) {
	var target = e.target;
	var indicatorCode = target.feature.properties.indicatorCode;
    var locationCode = target.feature.properties.locationCode
    // reset map table cell
    resetMapTableValue(locationCode, indicatorCode);
}

// dsr point value layer 
// point value -> (symbol = report value type) x (font size = report value)
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
			var mapTableValues = $('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]');

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
							"reportValue": $(mapValue).data('report-value'),
							"reportValueIcon": reportValueLabelIcon
						}
					};

					// add point label
					var geojsonPointFeature = createGeoJsonPointFeature(feature);
					locationValueLayer.addData(geojsonPointFeature);

					// add point
					var rawValue = $(mapValue).data('report-value-raw');
					var reportValueType = $(mapValue).data('report-value-type');
					feature.properties.reportValueType = $(mapValue).data('report-value-type');					
					if(reportValueType == 'NUMBER'){
						rawValue = parseFloat(rawValue);				
						var maxRawValue = getMaxRawValue();
						var percentageMaxRawValue = parseFloat(rawValue/maxRawValue);
						var reportValueSize = parseInt(percentageMaxRawValue*20)+10; //min: 10px max: 30px
					}
					feature.properties.reportValueSize = reportValueSize;
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

	// point value
	if(reportValueType){
		if(rawValue != null){
			var labelClassName = null;
			
			var pointValue = new L.Icon.Label.Default({					
					iconUrl: pointValueLabel,
					iconSize: new L.Point(20, 20),
					hideIcon: true,
					labelText: reportValue+'',
					labelAnchor: new L.Point(0, 0),
					wrapperAnchor: new L.Point(13, 5),
					// labelClassName: '',
					shadowUrl: null
			});
			
			switch(reportValueType){			
				case 'BOOL':
					if(rawValue)
						pointValue.options.labelClassName += 'report-value-marker-true'
					else
						pointValue.options.labelClassName += 'report-value-marker-false'
					break;
				case 'ENUM':
					pointValue.options.labelClassName += 'report-value-marker-enum'
					break;				
				case 'STRING':
					pointValue.options.labelClassName += 'report-value-marker-string'
					break;				
				case 'TEXT':
					pointValue.options.labelClassName += 'report-value-marker-text'
					break;			
				case 'NUMBER':
					var reportValueSize = feature.properties.reportValueSize;
					pointValue.options.labelFontSize = reportValueSize + 'px'
					pointValue.options.labelClassName += 'report-value-marker-number'
					break;
				default:
					pointValue.options.labelClassName += 'report-value-marker-na'
					break;
			}
			
			var geojsonMarkerOptions = {icon: pointValue};
			var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
		   	return geojsonMarker;	
		}
		else{
			pointValue = new L.Icon.Label.Default({					
				iconUrl: pointValueLabel,
				iconSize: new L.Point(20, 20),
				hideIcon: true,
				labelText: reportValue+'',
				labelAnchor: new L.Point(0, 0),
				wrapperAnchor: new L.Point(13, 5),
				labelClassName: rawValue == null ? 'report-value-marker-na' : 'report-value-marker-label',
				shadowUrl: null
			});
			var geojsonMarkerOptions = {icon: pointValue};
			var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
		   	return geojsonMarker;
		}
	}
	//point value label
	else{
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
		var geojsonMarkerOptions = {icon: pointValueLabel};
		var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
	   	return geojsonMarker;
	}
}

// dsr point value layer interactions

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