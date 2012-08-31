function mapMap(){
	var map = L.map('map').setView([-1.951069, lng=30.06134], 10);
	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		//TODO move this to message.properties?
		attribution: 'Map Data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> Contributors &mdash; ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a> &mdash; ' +
				'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
	}).addTo(map);
	
	mapLocations();
	mapDataLocations();	
}

function getDataLocations(){
	var dataLocations = [];
	$('.js-map-table-value.js-selected-value').each(function(){
	    var dataLocation = $(this).data('location-code');
	    if(dataLocations.indexOf(dataLocation) < 0){
	    	dataLocations.push(dataLocation+'');
	    }
	});	
}

function setMissingCoordinates(fosaId){
	//missing fosa coordinates
	$('.nav-table td[data-location-code="'+fosaId+'"]').append('&#185;');
}

function setMissingFacility(dataLocations, fosaDataLocations){
	for(var i = 0 ; i < dataLocations.length; i++){
		var dataLocation = dataLocations[i];
		if(fosaDataLocations.indexOf(dataLocation) < 0){
			//missing fosa facility
			$('.nav-table td[data-location-code="'+dataLocation+'"]').append('&#178;');
		}
	}
}

function dsrDataLocationPointToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var reportValueType = feature.properties.reportValueType;
	
	var reportValueIcon = null;
	var rawValueFontSize = null;
	var labelClassName = null;		
	var geojsonMarkerOptions = null;
	var geojsonMarker = null;
	
	reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: "${resource(dir:'images',file:'/maps/report-value-null.png')}",
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: reportValue+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(13, 5),
			labelClassName: 'report-value-marker',
			shadowUrl: null
	});
	
	switch(reportValueType){			
		case '${ValueType.BOOL}':
			if(rawValue)
				reportValueIcon.options.labelClassName += ' report-value-marker-true'
			else
				reportValueIcon.options.labelClassName += ' report-value-marker-false'
			break;				
		case '${ValueType.STRING}':
			reportValueIcon.options.labelClassName += ' report-value-marker-string'
			break;				
		case '${ValueType.TEXT}':
			reportValueIcon.options.labelClassName += ' report-value-marker-text'
			break;			
		case '${ValueType.NUMBER}':
			rawValue = parseFloat(rawValue);				
			var maxRawValue = getMaxRawValue();
			if(rawValue/maxRawValue > 0.5){
				rawValueFontSize = parseInt((rawValue/maxRawValue)*17)+17; //min: 17px max: 35px
				reportValueIcon.options.labelFontSize = rawValueFontSize + 'px'
			}				
			reportValueIcon.options.labelClassName += ' report-value-marker-number'
			break;				
		default:
			reportValueIcon.options.labelClassName += ' report-value-marker-na'
			break;
	}
	
	geojsonMarkerOptions = {icon: reportValueIcon};
	geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;		
}

function fctDataLocationPointToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var indicatorClass = feature.properties.indicatorClass;
	
	var geojsonMarkerOptions = {
	    radius: 10,
	    fillColor: "#ff7800",
	    color: "#000",
	    weight: 1,
	    opacity: 1,
	    fillOpacity: 0.8
	};
	
	switch(indicatorClass){			
		case 'indicator-worst':
			geojsonMarkerOptions.fillColor = "#d30000"				
			break;				
		case 'indicator-middle':
			geojsonMarkerOptions.fillColor = "#258cd5"
			break;
		case 'indicator-best':
			geojsonMarkerOptions.fillColor = "#65c029"
			break;
		default:
			geojsonMarkerOptions.fillColor = "#000"
			break;
	}
	
	geojsonMarker = L.circleMarker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;		
}

function onEachFeature(feature, layer) {
    if (feature.properties && feature.properties.popupContent) {
        layer.bindPopup(feature.properties.popupContent);
	}
}

function getMaxRawValue(){
	var maxRawValue = 0;
	$('.js-map-table-value.js-selected-value').each(function(){	        
		var valueType = $(this).children('div.report-value').data('report-value-type');
		if(valueType == '${ValueType.NUMBER}'){
			var value = $(this).children('div.report-value').data('report-value-raw');
			if(parseFloat(value) > maxRawValue)
				maxRawValue = parseFloat(value);
		}
	});		 	
	return (maxRawValue > 0 ? maxRawValue : 1);
}