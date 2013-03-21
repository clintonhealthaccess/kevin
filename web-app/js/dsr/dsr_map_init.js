var basePolygonLayer = L.geoJson(null, {
	style: function (feature){
		return feature.properties && feature.properties.style;
	},
	pointToLayer: polygonFeatureToLayer,
	onEachFeature: onEachPolygonFeature
});

var locationNameLayer = null
var locationValueNaLayer = null
var locationValueLayer = null
var locationLayers = []
var overlays = []

function dsrMap(){
	
	if(childrenCollectData || currentIndicatorIsCalculation){
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

		var mapTableIndicator = $('.js-map-table-indicator.js-selected-indicator');
		var indicatorName = $(mapTableIndicator).data('indicator-names');
		locationValueLayer = L.geoJson(null, geoJsonValueLayerOptions);
		locationLayers.push(locationValueLayer);
		
		locationValueNaLayer = L.geoJson(null, geoJsonValueLayerOptions);
		overlays["N/A"] = locationValueNaLayer;
		locationLayers.push(locationValueNaLayer);

		mapPolygons();
	}

    mapLayers = locationLayers;
    createTheMap(childrenCollectData);

	// alert("after everything ");
}

// dsr polygon value layer
// polygon value -> (symbol = report value type) x (font size = report value)

function mapPolygonValues(data){
	jQuery.each(data.features, function(i,dataFeature){
		
		var fosaid = dataFeature.id;

		if(!dataFeature.value){
			// fosa coordinates missing
		}
		else{
			// map 1 value for the indicator for the polygon
			var mapTableValue = $('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]');

			if(mapTableValue.size() == 1){
				
				var indicatorCode = $(mapTableValue).data('indicator-code');
				var indicatorName = $(mapTableValue).data('indicator-names');

				var polygonCoordinates = createPolygonCoordinates(dataFeature.value, false);
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

				// add value
				feature.properties.reportValueSize = getReportValueSize(reportValueType, rawValue);
				var geojsonPointFeature = createGeoJsonPointFeature(feature);
				locationValueLayer.addData(geojsonPointFeature);
			}
		}					
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

// dsr point value layer 
// point value -> (symbol = report value type) x (font size = report value)
// point value label -> location name

function mapPointValues(){
	var fosaLocations = [];
    var dataLocationUrl = getDataLocationUrl(mapUrl) + reportLocationCodes;
	jQuery.getJSON(dataLocationUrl, function(data){
	
		// TODO
		if(data == null){
			//alert("error dsr points");
			return;
		}
		
		jQuery.each(data.features, function(i,f){

			var fosaid = f.id;
			fosaLocations.push(fosaid+"");

			if(!f.value){
				// fosa coordinates missing
				missingFosaCoordinates(fosaid);
			}
			else{
				// map 1 point & point label per location
				var mapTableValues = $('.js-map-table-value.js-selected-value[data-location-code="'+fosaid+'"]');

				$(mapTableValues).each(function(index, mapTableValue){

					var mapValue = $(mapTableValue).children('div.report-value');
					var rawValue = $(mapValue).data('report-value-raw');
					var reportValue = $(mapValue).data('report-value')
					var reportValueType = $(mapValue).data('report-value-type');

					var pointCoordinates = createCoordinate(f.value, false)

					var feature = {
						"id": fosaid,
					    "geometry": {
					        "coordinates": pointCoordinates
						},
						"properties":{
							"locationCode": $(mapTableValue).attr('data-location-code'),
							"locationName": $(mapTableValue).data('location-names'),
							"indicatorClass": $(mapTableValue).data('indicator-class'),
							"indicatorCode": $(mapTableValue).data('indicator-code'),
							"indicatorName": $(mapTableValue).data('indicator-names'),
							"rawValue": rawValue,
							"reportValue": reportValue,
							"reportValueType": reportValueType,
							"reportValueIcon": reportValueLabelIcon
						}
					};

					// add value
					feature.properties.reportValueSize = getReportValueSize(reportValueType, rawValue);
					var geojsonPointFeature = createGeoJsonPointFeature(feature);
					if(rawValue != null) locationValueLayer.addData(geojsonPointFeature);
					else locationValueNaLayer.addData(geojsonPointFeature);

					// add value label
					locationNameLayer.addData(geojsonPointFeature);
				});
			}								
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

	var pointValue = new L.Icon.Label.Default({					
		iconUrl: pointValueLabel,
		iconSize: new L.Point(20, 20),
		hideIcon: true,
		labelText: reportValue+'',
		labelAnchor: new L.Point(0, 0),
		wrapperAnchor: new L.Point(13, 5),
		shadowUrl: null
	});

	// if rawValue != null, create a value marker, else create an 'na' marker
	if(rawValue != null){
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
	}
	else pointValue.options.labelClassName += 'report-value-marker-na'

	var geojsonMarkerOptions = {icon: pointValue};
	var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
}

function pointLabelFeatureToLayer(feature, latlng) {
	
	var locationName = feature.properties.locationName;
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var pointValueLabel = feature.properties.reportValueIcon;

	var pointValueLabel = new L.Icon.Label.Default({					
		iconUrl: pointValueLabel,
		iconSize: new L.Point(20, 20),
		hideIcon: true,
		labelText: locationName+'',
		labelAnchor: new L.Point(0, 0),
		// always position the label above the value
		wrapperAnchor: new L.Point(13, 15),
		labelClassName: 'report-value-marker-point-label',
		shadowUrl: null
	});
	var geojsonMarkerOptions = {icon: pointValueLabel};
	var geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
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