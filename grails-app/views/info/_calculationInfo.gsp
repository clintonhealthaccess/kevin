<div class="info">
	
	<div class="average">
		<span class="bold">Average value:</span>
		<span class="value">
			<g:if test="${info.value.numberValue}">
				<g:formatNumber number="${info.value.numberValue * 100}" format="#0.0"/>%
			</g:if>
			<g:else>N/A</g:else>
		</span>
		<div class="clear"></div>
	</div>
	
	<div>
		<span class="bold"><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></span>
		<div class="box span ${info.locations.size()>10?'hidden':''}">
			<g:if test="${groupLocations != null}">
				<g:each in="${groupLocations}" var="groupLocation">
					<a href="#" onclick="$(this).next().slideToggle(); return false;">
						${groupLocation.name}
					</a>
					<g:render template="/info/locations" model="[info: info, locations: info.getLocationsOfGroup(groupLocation)]"/>
					<div class="clear"></div>
				</g:each>
			</g:if>
			<g:else>
				<g:render template="/info/locations" model="[info: info, locations: info.locations]"/>
			</g:else>
		</div>
	</div>

	<!-- TODO display data element values -->
	
	<div>
		<span class="bold">
			<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Trend</a>
		</span>
		<div class="span box hidden">
			<g:render template="/chart/chart" model="[data: info.calculation.id, location: info.location.id]"/>
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(this).find('a.cluetip').cluetip(cluetipOptions);	
})
</script>
