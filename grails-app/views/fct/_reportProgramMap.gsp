<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>	
	<div id="map" class="map"></div>
	<!-- TODO move this to a map_init.js file -->
	<r:script>
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
					    	//TODO get hex code from a color map to set color styles
					        "style": {
								color: "#7FCDBB",			//"#99D8C9",
					        	weight: 4,
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
		$('div.js-map-location.selected').each(function(){
	        var dataLocation = $(this).data('location-code');
	        if(dataLocations.indexOf(dataLocation) < 0){
	        	dataLocations.push(dataLocation+'');
	        }
	    });
	    var fosaIds = dataLocations.join('|');
		
		var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+fosaIds;
		jQuery.getJSON(dataLocationUrl, function(data){
			
			var fosaDataLocations = []
			
			var geojsonPointLayer = L.geoJson(null, {pointToLayer: dataLocationPointToLayer, onEachFeature: onEachFeature}).addTo(map);				
			
			jQuery.each(data.features, function(i,f){
				
				$('.js-map-location.selected[data-location-code="'+f.properties.fosaid+'"]').each(function(index, mapLocation){					
					fosaDataLocations.push(f.properties.fosaid+'');
										
					var locationName = $(mapLocation).data('location-names');						
					var indicatorName = $(mapLocation).data('indicator-names');
					var indicatorClass = $(mapLocation).data('indicator-class');
					
					var mapValue = $(mapLocation).children('div.report-value');
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
						geojsonPointLayer.addData(geojsonPointFeature);
					}
					else{
						//TODO get rid of this
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
	
	function dataLocationPointToLayer(feature, latlng) {
		
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
	</r:script>
</div>