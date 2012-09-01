<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>	
	<div id="map" class="map"></div>
	<!-- TODO move this to a map_init.js file -->
	<r:script>
<<<<<<< Updated upstream
	var map = L.map('map').setView([-1.951069, lng=30.06134], 10);
	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
=======
	
	<!-- the map -->
	var baseLayer = L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
>>>>>>> Stashed changes
		maxZoom: 18,
		//TODO move this to message.properties?
		attribution: 'Map Data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> Contributors &mdash; ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a> &mdash; ' +
				'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
<<<<<<< Updated upstream
	}).addTo(map);
	
	mapLocations();
	mapDataLocations();
	
	function mapLocations(){
	
		<!-- Locations -->
		var locationUrl = "http://geocommons.com/datasets/265901/features.json?filter[code][][equals]=${currentLocation.code}";		
		jQuery.getJSON(locationUrl, function(data){
			jQuery.each(data.features, function(i,f){
				
				//TODO get rid of this and use f.geometry.coordinates		
				//create polygon coordinates
				var polygonCoordinates = []
				var coordinates = []
				var latlonRegex = /\[(\-|\d|\.)*,(\-|\d|\.)*\]/g;
=======
	});
		
	<!-- location layer -->
	var locationLayer = L.geoJson(null, {
		onEachFeature: onEachFeature,		
		style: function (feature) {
			return feature.properties && feature.properties.style;
		}
	});			
	var dataLocationValueLayer = null
	var dataLocationInfoLayer = null	
	var mapLayers = []
	
	<!-- location polygons -->
	if(${currentLocation.children != null && !currentLocation.children.empty}){
	    //var locationCodes = "${reportLocations.collect{it.code}.join('|')}";
	    var locationUrl = 
	    	"http://geocommons.com/datasets/265901/features.json?filter[code][][in]=${reportLocations.collect{it.code}.join('|')}";
	    addLocationLayerData(locationUrl, locationLayer, dataLocationValueLayer, dataLocationInfoLayer);	    
	    mapLayers = [baseLayer, locationLayer]
	}
	<!-- location polygon + data location markers -->
	else{
		//var locationCode = "${currentLocation.code}";
		var locationUrl = 
			"http://geocommons.com/datasets/265901/features.json?filter[code][][equals]=${currentLocation.code}";		
		addLocationLayerData(locationUrl, locationLayer, dataLocationValueLayer, dataLocationInfoLayer);		
		<!-- data location value layer -->
		dataLocationValueLayer = L.geoJson(null, {
			pointToLayer: dataLocationValuePointToLayer, 
			onEachFeature: onEachDataLocationValueFeature
		});		
		<!-- data location info layer --> 
		dataLocationInfoLayer = L.geoJson(null, {
			pointToLayer: dataLocationInfoPointToLayer, 
			onEachFeature: onEachFeature
		});		
		mapLayers = [baseLayer, locationLayer, dataLocationInfoLayer, dataLocationValueLayer]							
	}

	var map = L.map('map', {
		center: [-1.951069, lng=30.06134],
		zoom: 9,
		layers: mapLayers
	});
	
	if(dataLocationValueLayer != null && dataLocationInfoLayer != null){
		var overlays = {
			"Facilities": dataLocationInfoLayer
		};
		L.control.layers(null, overlays).addTo(map);
	}

	function addLocationLayerData(locationUrl, locationLayer, dataLocationValueLayer, dataLocationInfoLayer){
		<!-- Locations -->		
		jQuery.getJSON(locationUrl, function(data){				
			jQuery.each(data.features, function(i,f){
				
				//TODO get rid of this and use f.geometry.coordinates					
				//create polygon coordinates
				var polygonCoordinates = []
				var coordinates = []
				var latlonRegex = /\[(\-|\d|\.)*,(\-|\d|\.)*\]/g;				
				//TODO if coordinates == null
>>>>>>> Stashed changes
				$(f.properties.coordinates.match(latlonRegex)).each(function(){
					var coordinate = this;
					coordinate = this.replace(/(\[|\])/g,"");
					var lat = parseFloat(coordinate.split(',')[0]);
					var lon = parseFloat(coordinate.split(',')[1]);
					coordinates.push([lat, lon]);					
				});
				polygonCoordinates.push(coordinates);
				
				//create polygon geojson feature
				var geojsonPolygonFeature = {
						"id": f.properties.code,
					    "type": f.type,    
					    "geometry": {
					        "type": f.properties.featuretype,
					        "coordinates": polygonCoordinates
					    },
					    "properties": {
					        "style": {
								color: mapPolygonColors[i % mapPolygonColors.length],		//"#7FCDBB",	//"#99D8C9",
					        	weight: 4,
<<<<<<< Updated upstream
					        	fillColor: "#7FCDBB",		//"#99D8C9",
					            fillOpacity: 0.5 //0.4		//0.4
					        }
					    }
				};
				var geojsonPolygonLayer = L.geoJson(geojsonPolygonFeature, {style: geojsonPolygonFeature.properties.style}).addTo(map);
				map.fitBounds(geojsonPolygonLayer.getBounds());
			});
		});
	}						
	
	function mapDataLocations(){
	
		<!-- Data Locations -->
		var dataLocations = [];
		$('.js-map-table-value.js-selected-value').each(function(){
	        var dataLocation = $(this).data('location-code');
	        if(dataLocations.indexOf(dataLocation) < 0){
	        	dataLocations.push(dataLocation+'');
	        }
	    });
	    var fosaIds = dataLocations.join('|');
		
		var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+fosaIds;
=======
					        	fillColor: mapPolygonColors[i % mapPolygonColors.length],	//"#7FCDBB",	//"#99D8C9",
					            fillOpacity: 0.5,											//0.4
					            clickable: false					            
					        },
					    }
				};
				locationLayer.addData(geojsonPolygonFeature);							
			});
			
			if(dataLocationValueLayer != null && dataLocationInfoLayer != null){
				addDataLocationLayerData(dataLocationValueLayer, dataLocationInfoLayer);
			}
			
			<!-- map.fitBounds(...) start -->
			var currentLocationUrl = 
				"http://geocommons.com/datasets/265901/features.json?filter[code][][in]=${currentLocation.code}";		
			jQuery.getJSON(currentLocationUrl, function(data){
				jQuery.each(data.features, function(i,f){
					
					//TODO get rid of this and use f.geometry.coordinates					
					//create polygon coordinates
					var polygonCoordinates = []
					var coordinates = []
					var latlonRegex = /\[(\-|\d|\.)*,(\-|\d|\.)*\]/g;				
					//TODO if coordinates == null
					$(f.properties.coordinates.match(latlonRegex)).each(function(){
						var coordinate = this;
						coordinate = this.replace(/(\[|\])/g,"");
						var lat = parseFloat(coordinate.split(',')[0]);
						var lon = parseFloat(coordinate.split(',')[1]);
						coordinates.push([lat, lon]);					
					});
					polygonCoordinates.push(coordinates);
					
					//create polygon geojson feature
					var geojsonPolygonFeature = {
							"id": f.properties.code,
						    "type": f.type,    
						    "geometry": {
						        "type": f.properties.featuretype,
						        "coordinates": polygonCoordinates
						    },
						    "properties": {
						        "style": {
									color: mapPolygonColors[i % mapPolygonColors.length],		//"#7FCDBB",	//"#99D8C9",
						        	weight: 4,
						        	fillColor: mapPolygonColors[i % mapPolygonColors.length],	//"#7FCDBB",	//"#99D8C9",
						            fillOpacity: 0.5,											//0.4
						            clickable: false					            
						        },
						    }
					};
					var geojsonPolygon = L.geoJson(geojsonPolygonFeature);
					var latLngCenter = geojsonPolygon.getBounds().getCenter();
					map.setView(latLngCenter, 9);
					//map.fitBounds(latLngBounds);
				});
			
			});
			<!-- map.fitBounds(...) end -->
			
		});
	}
	
	function addDataLocationLayerData(dataLocationValueLayer, dataLocationInfoLayer){
		<!-- Data Location codes + FOSA ids -->
	    //var fosaIds = "${reportLocations.collect{it.code}.join('|')}";		
		var dataLocationUrl = 
			"http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]=${reportLocations.collect{it.code}.join('|')}";
>>>>>>> Stashed changes
		jQuery.getJSON(dataLocationUrl, function(data){
			
			var fosaDataLocations = []
			
			var geojsonPointLayer = L.geoJson(null, {pointToLayer: dataLocationPointToLayer, onEachFeature: onEachFeature}).addTo(map);				
			
			jQuery.each(data.features, function(i,f){
				
				$('.js-map-table-value.js-selected-value[data-location-code="'+f.properties.fosaid+'"]').each(function(index, mapTableValue){					
					fosaDataLocations.push(f.properties.fosaid+'');
										
					var locationName = $(mapTableValue).data('location-names');						
					var indicatorName = $(mapTableValue).data('indicator-names');
					var indicatorClass = $(mapTableValue).data('indicator-class');
					
					var mapValue = $(mapTableValue).children('div.report-value-number').children('div.report-value');
					var rawValue = $(mapValue).data('report-value-raw');
					var reportValue = $(mapValue).data('report-value');
					var reportValueType = $(mapValue).data('report-value-type');				
					
					if(f.geometry){
						//create point geojson feature
						var geojsonPointFeature = {
								"id": f.properties.fosaid,
							    "type": f.type,    
							    "geometry": {
							        "type": f.geometry.type,
							        "coordinates": f.geometry.coordinates
							    },
							    "properties": {
							    	"rawValue": rawValue,
							    	"reportValue": reportValue,
							    	"reportValueType": reportValueType,
							    	"locationName": locationName,
							    	"indicatorName": indicatorName,
							    	"indicatorClass": indicatorClass,
							        "popupContent": 'Location: '+locationName+'<br /> '+indicatorName+': '+reportValue
							    }
						};
<<<<<<< Updated upstream
						geojsonPointLayer.addData(geojsonPointFeature);
=======
						dataLocationValueLayer.addData(geojsonPointFeature);
						dataLocationInfoLayer.addData(geojsonPointFeature);
>>>>>>> Stashed changes
					}
					else{
						//missing fosa coordinates
						$('.nav-table td[data-location-code="'+f.properties.fosaid+'"]').append('&#185;');
					}
				});								
			});						
			
			for(var i = 0 ; i < dataLocations.length; i++){
				var dataLocation = dataLocations[i];
				if(fosaDataLocations.indexOf(dataLocation) < 0){
					//missing fosa facility
					$('.nav-table td[data-location-code="'+dataLocation+'"]').append('&#178;');
				}
			}									
		});
	}				
	
<<<<<<< Updated upstream
	function dataLocationPointToLayer(feature, latlng) {
		
=======
	function dataLocationValuePointToLayer(feature, latlng) {		
>>>>>>> Stashed changes
		var rawValue = feature.properties.rawValue;
		var reportValue = feature.properties.reportValue;
		var indicatorClass = feature.properties.indicatorClass;
		
		var geojsonMarkerOptions = {
		    radius: 8,
		    fillColor: mapMarkerColors[indicatorClass],
		    color: mapMarkerColors[indicatorClass],
		    weight: 1,
		    opacity: 1,
		    fillOpacity: 0.75
		};
		geojsonMarker = L.circleMarker(latlng, geojsonMarkerOptions);
       	return geojsonMarker;		
   	}
	
<<<<<<< Updated upstream
	function onEachFeature(feature, layer) {
=======
	function dataLocationInfoPointToLayer(feature, latlng) {
		var locationName = feature.properties.locationName;
			
		var reportValueIcon = null;
		var rawValueFontSize = null;
		//var labelClassName = null;		
		var geojsonMarkerOptions = null;
		var geojsonMarker = null;
		
		reportValueIcon = new L.Icon.Label.Default({					
				iconUrl: "${resource(dir:'images',file:'/maps/report-value-null.png')}",
				iconSize: new L.Point(20, 20),
				hideIcon: true,
				labelText: locationName+'',
				labelFontSize: '10px',
				labelAnchor: new L.Point(0, 0),
				wrapperAnchor: new L.Point(-10, 10),
				labelClassName: 'report-value-marker',
				shadowUrl: null
		});
		
		geojsonMarkerOptions = {icon: reportValueIcon};
		geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
       	return geojsonMarker;
   	}
	
	function onEachFeature(feature, layer){
		layer.options.geometry = feature.geometry;
	}
			
	function onEachDataLocationValueFeature(feature, layer) {
>>>>>>> Stashed changes
	    if (feature.properties && feature.properties.popupContent) {
	        layer.bindPopup(feature.properties.popupContent);
	        	        
	        layer.on("mouseover", function (e) {
	        	layer.openPopup();
            });
	        layer.on("mouseout", function (e) {
                map.closePopup();
            });
    	}
	}		
	</r:script>
</div>