<div class='map-wrap'>
	<g:if test="${viewSkipLevels.contains(currentLocation.level)}">
		<p class='nodata'>
			<g:message code="dsr.report.map.noinformation.exceptdistrict.label" />
		</p>
	</g:if>
	<g:else>
		<!-- TODO get rid of this -->
		<p class='nodata'>Map information coming soon!</p>
	</g:else>
	<!-- TODO map -->
	<div id="map" style="width: 968px; height: 400px"></div>
	<r:script>
		var map = L.map('map').setView([-1.951069, lng=30.06134], 9);
		L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
			maxZoom: 18,
			<!-- TODO message.properties -->
			attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
					'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
					'Imagery &copy; <a href="http://cloudmade.com">CloudMade</a>'
		}).addTo(map);		
	</r:script>
</div>