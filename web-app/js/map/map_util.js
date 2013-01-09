// TODO coordsToLatlng( <Array> coords) or coordsToLatlngs( <Array> coords, <Number> levelsDeep?) 
// 0 = array of points, 1 = arrays of array of points
function createPolygonCoordinates(feature, createLatLngCoordinates){
	// create polygon coordinates
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
			if(createLatLngCoordinates) latLng = L.latLng(lat, lng);
			else latLng = [lat, lng];
			coordinates.push(latLng);					
		});
		polygonCoordinates.push(coordinates);	
	}
	return polygonCoordinates;
}

function createGeoJsonPolygonFeature(feature){
	// create geojson polygon feature
	var geoJsonPolygonFeature = {
			"id": feature.id,
		    "type": "Feature",    
		    "geometry": {
		        "type": "Polygon",
		        "coordinates": feature.geometry.coordinates
		    },
		    "properties": {
		    	"locationCode": feature.properties.locationCode,
		    	"locationName": feature.properties.locationName,
		    	"style": feature.properties.style
		    }
	};
	return geoJsonPolygonFeature;
}

function createGeoJsonPointFeature(feature){
	// create geojson point feature
	var geoJsonPointFeature = {
			"id": feature.id,
		    "type": "Feature",    
		    "geometry": {
		        "type": "Point",
		        "coordinates": feature.geometry.coordinates
		    },
		    "properties": {
		    	"locationCode": feature.properties.locationCode,
		    	"locationName": feature.properties.locationName,
		    	"indicatorClass": feature.properties.indicatorClass,
		    	"indicatorCode": feature.properties.indicatorCode,
		    	"indicatorName": feature.properties.indicatorName,
		    	"rawValue": feature.properties.rawValue,
		    	"radiusValue": feature.properties.radiusValue,
		    	"reportValue": feature.properties.reportValue,
		    	"reportValueType": feature.properties.reportValueType,
		    	"reportValueIcon": feature.properties.reportValueIcon
		        // "popupContent": 'Location: '+locationName+'<br /> '+indicatorName+': '+reportValue
		    }
	};
	return geoJsonPointFeature;
}

// http://stackoverflow.com/questions/4969121/in-javascript-is-there-an-easy-way-to-sort-key-value-pairs-by-the-value-and-re
function sortMapByValue(O){
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

function createNorthSouthOffset(boundsLng, center){
	var boundsLng = center.lng+((boundsLng-center.lng)/4);
	return [center.lat, boundsLng];
}

function createEastWestOffset(boundsLat, center){
	var boundsLat = center.lat+((boundsLat-center.lat)/4);
	return [boundsLat, center.lng];
}

function getMinRawValue(){
	var minRawValue;
	$('div.js-map-table-value.js-selected-value').each(function(index){
		var value = $(this).children('div.report-value').data('report-value-raw');
		if(index == 0) 
			minRawValue = parseFloat(value); 
		if(parseFloat(value) < minRawValue)
			minRawValue = parseFloat(value);
	});
	return minRawValue;
}

function getMaxRawValue(){
	var maxRawValue = 0;
	$('div.js-map-table-value.js-selected-value').each(function(){	        
		var value = $(this).children('div.report-value').data('report-value-raw');
		if(parseFloat(value) > maxRawValue)
			maxRawValue = parseFloat(value);
	});
	maxRawValue = maxRawValue > 0 ? maxRawValue : 1;
	return maxRawValue;
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
		if(fosaLocations.indexOf(location) < 0)
			$('.nav-table td[data-location-code="'+location+'"]').append('&#178;');
	}
}

function highlightMapTableLocation(locationCode){
	// highlight map table location row
    var mapTableRow = $('.js-map-table-location[data-location-code="'+locationCode+'"]').parent('td').parent('tr');
    $(mapTableRow).addClass('highlighted-table');
}

function resetMapTableLocation(locationCode){
	// reset map table location row
	var mapTableRow = $('.js-map-table-location[data-location-code="'+locationCode+'"]').parent('td').parent('tr');
    $(mapTableRow).removeClass('highlighted-table');
}

function highlightMapTableValue(locationCode, indicatorCode){
	// highlight map table report value
    var mapTableValue = $('.js-map-table-value[data-location-code="'+locationCode+'"][data-indicator-code="'+indicatorCode+'"]').parent('td');
    $(mapTableValue).addClass('highlighted-table');
}

function resetMapTableValue(locationCode, indicatorCode){
	// reset map table report value
    var mapTableValue = $('.js-map-table-value[data-location-code="'+locationCode+'"][data-indicator-code="'+indicatorCode+'"]').parent('td');
    $(mapTableValue).removeClass('highlighted-table');
}