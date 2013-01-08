<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<div class='map-wrap'>
	%{-- TODO display if children do not collect data or selected indicator is not a calculation" --}%
	<g:if test="${mapSkipLevels != null && mapSkipLevels.contains(currentLocation.level)}">
		<p class='nodata'>
			<g:message code="dsr.report.map.selectdistrict.label" />
		</p>
	</g:if>
	<div id="map" class="map" />
	
	<r:script>
		var childrenCollectData = ${currentLocation.getChildren(locationSkipLevels) == null || currentLocation.getChildren(locationSkipLevels).empty};
		var currentLocationCode = "${currentLocation.code}";
		var reportLocationCodes = "${reportLocations.collect{it.code}.join('|')}";
		var reportValueLabelIcon = "${resource(dir:'images',file:'/maps/report-value-null.png')}";
		dsrMap(childrenCollectData, currentLocationCode, reportLocationCodes);
	</r:script>
</div>