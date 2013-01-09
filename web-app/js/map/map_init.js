var map
var mapLayers = []

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

// base polygon layer
// polygon -> color = blue/green #99D8C9/#2CA25F
// polygon label -> location name

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

			// color fillColor/color
			// blue/green #99D8C9/#2CA25F
			// orange #FDAE6B/#E6550D
			// purple #BCBDDC/#756BB1
			var polygonStyle = {
				color: "#2CA25F",
				weight: 1.5,
				fillColor: "#99D8C9",
			    fillOpacity: 0.75
			};

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

// base polygon value layer interactions

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
				// highlight map polygon
				layerData.setStyle({
			        fillOpacity: 0.85,
			        weight: 3
			    });
			}
		});
		// highlight map table row
		highlightMapTableLocation(locationCode);
	}
	else{
		// highlight map polygon
		polygon.setStyle({
	        fillOpacity: 0.85,
	        weight: 3
	    });
	}
}

function resetPolygonFeature(e) {
	var polygon = e.target;
    var polygonLabel = polygon.feature.properties.reportValueIcon
    var locationCode = polygon.feature.properties.locationCode
    if(polygonLabel){
    	// reset map table row
    	resetMapTableLocation(locationCode);
    }
	else{
		// reset map polygon
		basePolygonLayer.resetStyle(polygon);	
	}

}