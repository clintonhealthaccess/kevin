<%@ page import="org.chai.kevin.data.Calculation" %>
<div class='map-wrap'>
	<g:set var="childrenCollectData" value="${currentLocation.getChildren(locationSkipLevels) == null || currentLocation.getChildren(locationSkipLevels).empty}"/>
	<g:set var="currentIndicatorIsCalculation" value="${currentIndicators.findAll{Calculation.class.isAssignableFrom(it.data.class)} == currentIndicators}"/>
	<g:if test="${!childrenCollectData && !currentIndicatorIsCalculation}">
		<p class='nodata'>
			<g:message code="dsr.report.map.selectlocation.label" />
		</p>
	</g:if>
	<div id="map" class="map" />
	
	<r:script>
		var childrenCollectData = ${childrenCollectData};
		var currentIndicatorIsCalculation = ${currentIndicatorIsCalculation};
		var currentLocationCode = "${currentLocation.code}";
		var reportLocationCodes = "${reportLocations.collect{it.code}.join(',')}";
		var reportValueLabelIcon = "${resource(dir:'images',file:'/maps/report-value-null.png')}";
		var mapUrl = "${createLink(controller:controllerName, action:'map')}";
		dsrMap();
	</r:script>
</div>