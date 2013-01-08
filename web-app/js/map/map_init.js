var map;
var mapLayers = [];
var basePolygonLayer = L.geoJson(null, {
	style: function (feature){
		return feature.properties && feature.properties.style;
	},
	pointToLayer: polygonFeatureToLayer,
	onEachFeature: onEachPolygonFeature
});

function createTheMap(){
	var mapBaseLayer = L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		// TODO move this to message.properties?
		attribution: 'Map Data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> Contributors &mdash; ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a> &mdash; ' +
				'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
	});
	var baseLayers = [mapBaseLayer, basePolygonLayer]
	mapLayers = baseLayers.concat(mapLayers)
	map = L.map('map', {
		center: [-1.951069, lng=30.06134],
		zoom: 9,
		layers: mapLayers,
		touchZoom: false,
		scrollWheelZoom: false
		// attributionControl: false
	});
	L.control.scale().addTo(map);
	// alert("after creating the map ");
}

function mapTheMap(mapLocationValueLayer){
	
	var width = basePolygonLayer.getBounds().getNorthEast().lat-basePolygonLayer.getBounds().getSouthWest().lat;
	var height = basePolygonLayer.getBounds().getNorthEast().lng-basePolygonLayer.getBounds().getSouthWest().lng;
	
	var area = width*height;
	if(mapLocationValueLayer && area > 0.5) map.setView(basePolygonLayer.getBounds().getCenter(), 9);
	else map.fitBounds(basePolygonLayer.getBounds());
	
	// alert("after creating the map ");
}

function mapPolygons(childrenCollectData, currentLocationCode, reportLocationCodes){
	
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

			var polygonCoordinates = createPolygonCoordinates(dataFeature, false);

			// add polygon
			var polygonFeature = {
				"id": fosaid,
				"geometry": {
			    	"coordinates": polygonCoordinates
				},
				"properties":{
					"locationCode": fosaid,
					"locationName": locationName
				}
			};
			var geojsonPolygonFeature = createGeoJsonPolygonFeature(polygonFeature);
			basePolygonLayer.addData(geojsonPolygonFeature);
			
			// add polygon label
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

// polygon label layer
// poylygon label = location name
function polygonFeatureToLayer(feature, latlng) {

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

function onEachPolygonFeature(feature, layer) {
    layer.on({
        mouseover: highlightPolygonFeature,
        mouseout: resetPolygonFeature,
        // click: zoomToFeature
    });
}

function highlightPolygonFeature(e) {
    var polygon = e.target;
    var polygonLabel = polygon.feature.properties.reportValueIcon
    var locationCode = polygon.feature.properties.locationCode
	if(polygonLabel){
		basePolygonLayer.eachLayer(function (layerData) {
			var fLocationCode = layerData.feature.properties.locationCode;
			var fPolygonLabel = layerData.feature.properties.reportValueIcon
			if(fLocationCode == locationCode && !fPolygonLabel){
				layerData.setStyle({
			        fillOpacity: 0.85,
			        weight: 3
			    });
			}
		});
	}
	else{
		polygon.setStyle({
	        fillOpacity: 0.85,
	        weight: 3
	    });
	}
}

function resetPolygonFeature(e) {
	var polygon = e.target;
    var polygonLabel = polygon.feature.properties.reportValueIcon
	if(!polygonLabel)
		basePolygonLayer.resetStyle(polygon);
}

// function zoomToFeature(e) {
//    map.fitBounds(e.target.getBounds());
// }

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
		    	// color fillColor/color
		    	// blue/green #99D8C9/#2CA25F
		    	// orange #FDAE6B/#E6550D
		    	// purple #BCBDDC/#756BB1
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

// TODO http://stackoverflow.com/questions/4969121/in-javascript-is-there-an-easy-way-to-sort-key-value-pairs-by-the-value-and-re
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