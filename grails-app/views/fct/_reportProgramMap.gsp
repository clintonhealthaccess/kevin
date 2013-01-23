<div class='map-wrap'>	
	<div id="map" class="map" />
	
	<r:script>
		var childrenCollectData = ${currentLocation.getChildren(locationSkipLevels) == null || currentLocation.getChildren(locationSkipLevels).empty};
		var currentLocationCode = "${currentLocation.code}";
		var reportLocationCodes = "${reportLocations.collect{it.code}.join('|')}";
		var reportValueLabelIcon = "${resource(dir:'images',file:'/maps/report-value-null.png')}";
		fctMap(childrenCollectData, currentLocationCode, reportLocationCodes);
	</r:script>
</div>