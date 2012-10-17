var map;
var mapLayers = [];

function createTheMap(baseLocationLayer){
	var mapBaseLayer = L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		//TODO move this to message.properties?
		attribution: 'Map Data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> Contributors &mdash; ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a> &mdash; ' +
				'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
	});
	var baseLayers = [mapBaseLayer]
	mapLayers = baseLayers.concat(mapLayers)
	map = L.map('map', {
		center: [-1.951069, lng=30.06134],
		zoom: 9,
		layers: mapLayers,
		touchZoom: false,
		scrollWheelZoom: false
		//attributionControl: false
	});
	//alert("after creating the map ");
}

function mapTheMap(baseLocationLayer, mapLocationValueLayer, mapDataLocationValueLayer){
	
	var width = baseLocationLayer.getBounds().getNorthEast().lat-baseLocationLayer.getBounds().getSouthWest().lat;
	var height = baseLocationLayer.getBounds().getNorthEast().lng-baseLocationLayer.getBounds().getSouthWest().lng;
	var area = width*height;

	if(mapLocationValueLayer && area > 0.5){
		map.setView(baseLocationLayer.getBounds().getCenter(), 9);
	}
	else if(mapDataLocationValueLayer){
		//TODO add area?
		map.fitBounds(baseLocationLayer.getBounds());
	}
	else{
		map.fitBounds(baseLocationLayer.getBounds());
	}
	
//	alert("after mapping the map"+
//		"\nne lat="+baseLocationLayer.getBounds().getNorthEast().lat+
//		", lng="+baseLocationLayer.getBounds().getNorthEast().lng+
//		"\nsw lat="+baseLocationLayer.getBounds().getSouthWest().lat+
//		", lng="+baseLocationLayer.getBounds().getSouthWest().lng+
//		"\nw: "+width+"\nh: "+height+"\narea: "+(width*height));
}

function highlightFeature(e) {
    var layer = e.target;
    layer.setStyle({
        fillOpacity: 1
    });
}
function resetFeature(e) {
	var layer = e.target;
	//TODO use resetStyle
    layer.setStyle({
		fillOpacity: 0.75
    });
}
//function zoomToFeature(e) {
//    map.fitBounds(e.target.getBounds());
//}
function onEachBaseLocationLayerFeature(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetFeature,
        //click: zoomToFeature
    });
}

function createPolygonCoordinates(feature, createLatLngCoordinates){
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
			var lng = parseFloat(coordinate.split(',')[1]);
			
			var latLng = null;
			if(createLatLngCoordinates)
				latLng = L.latLng(lat, lng);
			else
				latLng = [lat, lng];
			coordinates.push(latLng);					
		});
		polygonCoordinates.push(coordinates);	
	}
	return polygonCoordinates;
}

function sortMapByValue(O){
	//http://stackoverflow.com/questions/4969121/in-javascript-is-there-an-easy-way-to-sort-key-value-pairs-by-the-value-and-re
	var A= [];
    for(var p in O){
        if(O.hasOwnProperty(p)) A.push([p, O[p]]);
    }
	A.sort(function(a, b){
        var a1= a[1];
        var b1= b[1];
        return a1-b1;
    });
    for(var i= 0, L= A.length; i<L; i++){
    	A[i]= A[i][0];
    }
    //return A;
    return A.reverse();
}

function createEastWestOffsetCoordinates(bounds, center){
	var boundsLat = bounds.lat;
	var centerLat = center.lat;
	boundsLat = centerLat+((boundsLat-centerLat)/4);
	
	var boundsLng = bounds.lng;
	var centerLng = center.lng;
	//boundsLng = centerLng+((boundsLng-centerLng)/4);
	boundsLng = centerLng;
	return [boundsLat, boundsLng];
}

function createNorthSouthOffsetCoordinates(bounds, center){
	var boundsLat = bounds.lat;
	var centerLat = center.lat;
	//boundsLat = centerLat+((boundsLat-centerLat)/4);
	boundsLat = centerLat;
	
	var boundsLng = bounds.lng;
	var centerLng = center.lng;
	boundsLng = centerLng+((boundsLng-centerLng)/4);
	return [boundsLat, boundsLng];
}

function createGeoJsonPolygonFeature(feature, polygonCoordinates){
	//create geojson polygon feature
	var geoJsonPolygonFeature = {
			"id": feature.properties.code,
		    "type": "Feature",    
		    "geometry": {
		        "type": "Polygon",
		        "coordinates": polygonCoordinates
		    },
		    "properties": {
		    	//color fillColor/color
		    	//blue/green "#99D8C9"/"#2CA25F"
		    	//orange "#FDAE6B"/"#E6550D"
		    	//purple "#BCBDDC"/"#756BB1"
		        "style": {
					color: "#2CA25F",
		        	weight: 1.5,
		        	fillColor: "#99D8C9",
		            fillOpacity: 0.75
		        }
		    }
	};
	return geoJsonPolygonFeature;
}

function createGeoJsonPointFeature(feature){
	//create geojson point feature
	var geoJsonPointFeature = {
			"id": feature.id,
		    "type": "Feature",    
		    "geometry": {
		        "type": "Point",
		        "coordinates": feature.geometry.coordinates
		    },
		    "properties": {
		    	"locationName": feature.properties.locationName,
		    	"indicatorName": feature.properties.indicatorName,
		    	"indicatorClass": feature.properties.indicatorClass,
		    	"rawValue": feature.properties.rawValue,
		    	"reportValue": feature.properties.reportValue,
		    	"reportValueType": feature.properties.reportValueType,
		    	"reportValueIcon": feature.properties.reportValueIcon
		        //"popupContent": 'Location: '+locationName+'<br /> '+indicatorName+': '+reportValue
		    }
	};
	return geoJsonPointFeature;
}

function dsrDataLocationValuePointToLayer(feature, latlng) {
	
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

function dsrDataLocationInfoPointToLayer(feature, latlng) {
	
	var locationName = feature.properties.locationName;
		
	var reportValueIcon = feature.properties.reportValueIcon;
	var rawValueFontSize = null;
	var geojsonMarkerOptions = null;
	var geojsonMarker = null;
	
	reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: reportValueIcon,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: locationName+'',
			labelFontSize: '10px',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(13, 17),
			//TODO change class name?
			labelClassName: 'report-value-marker',
			shadowUrl: null
	});
	
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

function fctBaseLocationInfoPointToLayer(feature, latlng) {
	
	var locationName = feature.properties.locationName;
		
	var reportValueIcon = feature.properties.reportValueIcon;
	var rawValueFontSize = null;
	var geojsonMarkerOptions = null;
	var geojsonMarker = null;
	
	reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: reportValueIcon,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: locationName+'',
			labelFontSize: '10px',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(-10, 10),
			//TODO change class name?
			labelClassName: 'report-value-marker',
			shadowUrl: null
	});
	
	geojsonMarkerOptions = {icon: reportValueIcon};
	geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
}

function fctLocationValuePointToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var indicatorClass = feature.properties.indicatorClass;
	
	//TODO max: 35px?
	var rawValueRadius = parseInt(rawValue*20)+10; //min: 10px max: 30px
	rawValueRadius = rawValueRadius < 10 ? 10 : rawValueRadius;
	rawValueRadius = rawValueRadius > 30 ? 30 : rawValueRadius;
	
	var geojsonMarkerOptions = {
		radius: rawValueRadius,
	    fillColor: mapMarkerColors[indicatorClass],
	    color: mapMarkerColors[indicatorClass],
	    weight: 1,
	    opacity: 1,
	    fillOpacity: 0.75
	};
	geojsonMarker = L.circleMarker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;		
}

function fctLocationInfoPointToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	
	var reportValueIcon = feature.properties.reportValueIcon;
	var rawValueFontSize = null;
	var geojsonMarkerOptions = null;
	var geojsonMarker = null;
	
	reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: reportValueIcon,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: reportValue+'',
			labelFontSize: '20px',
			labelAnchor: new L.Point(0, 0),
			//TODO rawValue < 100, rawValue < 1,000
			wrapperAnchor: rawValue < 10 ? new L.Point(6, 10) : (rawValue < 100 ? new L.Point(11, 10) : new L.Point(17, 10)),
			//TODO change class name?
			labelClassName: 'report-value-marker',
			shadowUrl: null
	});
	
	geojsonMarkerOptions = {icon: reportValueIcon};
	geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
}

function fctDataLocationValuePointToLayer(feature, latlng) {
	
	var rawValue = feature.properties.rawValue;
	var reportValue = feature.properties.reportValue;
	var indicatorClass = feature.properties.indicatorClass;
	
	var geojsonMarkerOptions = {
	    radius: 10,
	    fillColor: mapMarkerColors[indicatorClass],
	    color: mapMarkerColors[indicatorClass],
	    weight: 1,
	    opacity: 1,
	    fillOpacity: 0.75
	};
	geojsonMarker = L.circleMarker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;		
}

function fctDataLocationInfoPointToLayer(feature, latlng) {
	
	var locationName = feature.properties.locationName;
		
	var reportValueIcon = feature.properties.reportValueIcon;
	var rawValueFontSize = null;
	var geojsonMarkerOptions = null;
	var geojsonMarker = null;
	
	reportValueIcon = new L.Icon.Label.Default({					
			iconUrl: reportValueIcon,
			iconSize: new L.Point(20, 20),
			hideIcon: true,
			labelText: locationName+'',
			labelFontSize: '10px',
			labelAnchor: new L.Point(0, 0),
			wrapperAnchor: new L.Point(-12, 7),
			//TODO change class name?
			labelClassName: 'report-value-marker',
			shadowUrl: null
	});
	
	geojsonMarkerOptions = {icon: reportValueIcon};
	geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
   	return geojsonMarker;
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

//add data to a data point
//function onEachFeature(feature, layer){
//	layer.options.geometry = feature.geometry;
//}
//add popup to a data point
//function onEachFeature(feature, layer) {
//	if (feature.properties && feature.properties.popupContent) {
//	  layer.bindPopup(feature.properties.popupContent);
//	}
//}
//add events to a data point
//function onEachDataLocationValueFeature(feature, layer) {
//	layer.on("mouseover", function (e) {
//		layer.openPopup();
//	});
//	layer.on("mouseout", function (e) {
//	    map.closePopup();
//	});
//}