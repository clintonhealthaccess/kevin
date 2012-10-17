var map;
function theMap(){
	<!-- the map -->
	var baseLayer = L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		//TODO move this to message.properties?
		attribution: 'Map Data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> Contributors &mdash; ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a> &mdash; ' +
				'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
	});
	var mapLayers = [baseLayer]
	map = L.map('map', {
		center: [-1.951069, lng=30.06134],
		zoom: 9,
		layers: mapLayers,
		touchZoom: false,
		scrollWheelZoom: false
		//attributionControl: false
	});
}

function createPolygonCoordinates(feature){
	//create polygon coordinates
	//TODO get rid of this and use feature.geometry.coordinates
	var polygonCoordinates = []
	var coordinates = []
	var latlonRegex = /\[(\-|\d|\.)*,(\-|\d|\.)*\]/g;
	if(feature.properties && feature.properties.coordinates){
		$(feature.properties.coordinates.match(latlonRegex)).each(function(){
			var coordinate = this;
			coordinate = this.replace(/(\[|\])/g,"");
			var lat = parseFloat(coordinate.split(',')[0]);
			var lon = parseFloat(coordinate.split(',')[1]);
			coordinates.push([lat, lon]);					
		});
		polygonCoordinates.push(coordinates);	
	}
	return polygonCoordinates;
}

function createGeoJsonPolygonFeature(feature, polygonCoordinates){
	//create geojson polygon feature
	var geoJsonPolygonFeature = {
			"id": feature.properties.code,
		    "type": feature.type,    
		    "geometry": {
		        "type": feature.properties.featuretype,
		        "coordinates": polygonCoordinates
		    },
		    "properties": {
		    	//TODO switch to neutral polygon color and fillColor
		        "style": {
					color: "#7FCDBB",			//"#99D8C9",
		        	weight: 4,
		        	fillColor: "#7FCDBB",		//"#99D8C9",
		            fillOpacity: 0.5 //0.4		//0.4
		        }
		    }
	};
	return geoJsonPolygonFeature;
}

function onEachFeature(feature, layer) {
    if (feature.properties && feature.properties.popupContent) {
        layer.bindPopup(feature.properties.popupContent);
	}
}

function createGeoJsonPointFeature(feature, locationName, indicatorName, rawValue, reportValue, reportValueType, reportValueIcon){
	//create geojson point feature
	var geoJsonPointFeature = {
			"id": feature.properties.fosaid,
		    "type": feature.type,    
		    "geometry": {
		        "type": feature.geometry.type,
		        "coordinates": feature.geometry.coordinates
		    },
		    "properties": {
		    	"rawValue": rawValue,
		    	"reportValue": reportValue,
		    	"reportValueType": reportValueType,
		    	"reportValueIcon": reportValueIcon,
		    	"locationName": locationName,
		    	"indicatorName": indicatorName,
		        "popupContent": 'Location: '+locationName+'<br /> '+indicatorName+': '+reportValue
		    }
	};
	return geoJsonPointFeature;
}

function dataLocationPointToLayer(feature, latlng) {
	
	//the heart and soul of the map
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var reportValueType = feature.properties.reportValueType;
	var reportValueIcon = feature.properties.reportValueIcon;
	
	var rawValueFontSize = null;
	var labelClassName = null;		
	var geojsonMarkerOptions = null;
	var geojsonMarker = null;
	
	reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: reportValueIcon,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: reportValue+'',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(13, 5),
			//labelClassName: '',
			shadowUrl: null
	});
	
	switch(reportValueType){			
		case 'BOOL':
			if(rawValue)
				reportValueIcon.options.labelClassName += 'report-value-marker-true'
			else
				reportValueIcon.options.labelClassName += 'report-value-marker-false'
			break;
		case 'STRING':
			reportValueIcon.options.labelClassName += 'report-value-marker-string'
			break;				
		case 'TEXT':
			reportValueIcon.options.labelClassName += 'report-value-marker-text'
			break;			
		case 'NUMBER':
			rawValue = parseFloat(rawValue);				
			var maxRawValue = getMaxRawValue();
			if(rawValue/maxRawValue > 0.5){
				rawValueFontSize = parseInt((rawValue/maxRawValue)*20)+10; //min: 10px max: 30px
				reportValueIcon.options.labelFontSize = rawValueFontSize + 'px'
			}
			reportValueIcon.options.labelClassName += 'report-value-marker-number'
			break;	
		default:
			reportValueIcon.options.labelClassName += 'report-value-marker-na'
			break;
	}
	
	geojsonMarkerOptions = {icon: reportValueIcon};
	geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;		
}
function getMaxRawValue(){
	var maxRawValue = 0;
	$('div.js-map-table-value.js-selected-value').each(function(){	        
		var value = $(this).children('div.report-value').data('report-value-raw');
		if(parseFloat(value) > maxRawValue)
			maxRawValue = parseFloat(value);
	});
	return (maxRawValue > 0 ? maxRawValue : 1);
}

function missingFosaCoordinates(fosaid){
	//fosa coordinates missing
	$('.nav-table td[data-location-code="'+fosaid+'"]').append('&#185;');
}
function missingFosaLocations(fosaLocations, locations){
	//fosa locations missing
	for(var i = 0 ; i < locations.length; i++){
		var location = locations[i];
		//fosa location missing
		if(fosaLocations.indexOf(location) < 0){
			$('.nav-table td[data-location-code="'+location+'"]').append('&#178;');
		}
	}
}