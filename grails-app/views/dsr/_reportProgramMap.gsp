<div class='map-wrap'>
	<g:if test="${viewSkipLevels.contains(currentLocation.level)}">
		<p class='nodata'>
			<g:message code="dsr.report.map.noinformation.exceptdistrict.label" />
		</p>
	</g:if>
	<div id="map" style="width: 968px; height: 500px"></div>
	<!-- TODO move this to a map_init.js file -->
	<r:script>
	var map = L.map('map').setView([-1.951069, lng=30.06134], 10);
	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		//TODO move this to message.properties?
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
				'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
	}).addTo(map);
	
	if(${!viewSkipLevels.contains(currentLocation.level)}){
		mapLocations();
		mapDataLocations();									
	}		
	
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
		$('div.js-map-location[data-target-code="${currentTarget.code}"]').each(function(){
	        var dataLocation = $(this).data('location-code');
	        dataLocations.push(dataLocation+'');
	    });
	    var fosaIds = dataLocations.join('|');
		
		var dataLocationUrl = "http://geocommons.com/datasets/262585/features.json?filter[fosaid][][in]="+fosaIds;
		jQuery.getJSON(dataLocationUrl, function(data){
			
			var fosaDataLocations = []
			
			var geojsonPointLayer = L.geoJson(null, {pointToLayer: dataLocationPointToLayer, onEachFeature: onEachFeature}).addTo(map);				
			
			jQuery.each(data.features, function(i,f){
				
				fosaDataLocations.push(f.properties.fosaid+'');
				
				var mapLocation = $('div.js-map-location[data-location-code="'+f.properties.fosaid+'"][data-target-code="${currentTarget.code}"]');			
				var locationName = $(mapLocation).data('location-names');
				
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
						        "popupContent": 'Location: '+locationName+'<br /> ${i18n(field: currentTarget.names)}: '+reportValue
						    }
					};
					geojsonPointLayer.addData(geojsonPointFeature);
				}
				else{
					//TODO get rid of this
					//missing fosa data location coordinates
					$('.nav-table td[data-location-code="'+f.properties.fosaid+'"]').append('*');
				}
			});
			
			for(var i = 0 ; i < dataLocations.length; i++){
				var dataLocation = dataLocations[i];
				if(fosaDataLocations.indexOf(dataLocation) < 0){
					//missing fosa data location
					$('.nav-table td[data-location-code="'+dataLocation+'"]').append('*');
				}
			}									
		});
	}				
	
	function dataLocationPointToLayer(feature, latlng) {
		
		var rawValue = feature.properties.rawValue;
		var reportValue = feature.properties.reportValue;
		var reportValueType = feature.properties.reportValueType;				
		
		var reportValueIcon = null;			
		var geojsonMarkerOptions = null;
		var geojsonMarker = null;
		
		switch(reportValueType){
			//TODO use ValueType enum
			case 'BOOL':
				if(rawValue){
					reportValueIcon = L.icon({
						iconUrl: "${resource(dir:'images',file:'/maps/report-value-true.png')}",
						iconSize: new L.Point(20, 20)
					});
				}
				else{
					reportValueIcon = L.icon({
						iconUrl: "${resource(dir:'images',file:'/maps/report-value-false.png')}",
						iconSize: new L.Point(20, 20)
					});
				}
				break;
			case 'NUMBER':
				var maxRawValue = getMaxRawValue();
				var rawValueSize = parseInt((parseFloat(rawValue)/maxRawValue)*25)+25; //min: 25px max: 50px
				reportValueIcon = new L.Icon.Label.Default({							
						iconUrl: "${resource(dir:'images',file:'/maps/report-value-null.png')}",
						iconSize: new L.Point(20, 20),
						hideIcon: true,
						labelText: reportValue,
						labelFontSize: rawValueSize + 'px',
						labelAnchor: new L.Point(0, 8),
						wrapperAnchor: new L.Point(13, 36),
						labelClassName: feature.id + '-report-value-marker-icon-label',
						shadowUrl: null
				});
				break;
			case 'STRING':
			case 'TEXT':
				reportValueIcon = new L.Icon.Label.Default({							
						iconUrl: "${resource(dir:'images',file:'/maps/report-value-null.png')}",
						iconSize: new L.Point(20, 20),
						hideIcon: true,
						labelText: reportValue,
						labelAnchor: new L.Point(0, 8),
						wrapperAnchor: new L.Point(13, 36),
						labelClassName: feature.id + '-report-value-marker-icon-label',
						shadowUrl: null
				});
				break;
			default:
				reportValueIcon = L.icon({
						iconUrl: "${resource(dir:'images',file:'/maps/report-value-null.png')}",
						iconSize: new L.Point(20, 20)
				});
				break;
		}
		geojsonMarkerOptions = {icon: reportValueIcon};
		geojsonMarker = L.marker(latlng, geojsonMarkerOptions);
       	return geojsonMarker;		
   	}
	
	function onEachFeature(feature, layer) {
	    if (feature.properties && feature.properties.popupContent) {
	        layer.bindPopup(feature.properties.popupContent);
    	}
	}
	
	//TODO only do this once or get rid of this
	function getMaxRawValue(){
		var maxRawValue = 0;			
		$('div.js-map-location[data-target-code="${currentTarget.code}"]').each(function(){	        
	        var valueType = $(this).children('div.report-value').data('report-value-type');
	        var value = $(this).children('div.report-value').data('report-value-raw');
	        if(valueType == 'NUMBER' && parseFloat(value) > maxRawValue){
				maxRawValue = parseFloat(value);
			}
	 	});		 	
	 	return (maxRawValue > 0 ? maxRawValue : 1);
	 }			
	</r:script>
</div>