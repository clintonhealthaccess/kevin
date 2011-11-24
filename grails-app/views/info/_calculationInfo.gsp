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
		<div class="box span ${info.organisations.size()>10?'hidden':''}">
			<g:if test="${groupOrganisations != null}">
				<g:each in="${groupOrganisations}" var="groupOrganisation">
					<a href="#" onclick="$(this).next().slideToggle(); return false;">
						${groupOrganisation.name}
					</a>
					<g:render template="/info/organisations" model="[info: info, organisations: info.getOrganisationsOfGroup(groupOrganisation)]"/>
					<div class="clear"></div>
				</g:each>
			</g:if>
			<g:else>
				<g:render template="/info/organisations" model="[info: info, organisations: info.organisations]"/>
			</g:else>
		</div>
	</div>

	<!-- TODO display data element values -->
	
	<div>
		<span class="bold">
			<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Trend</a>
		</span>
		<div class="span box hidden">
			<g:render template="/chart/chart" model="[data: info.calculation.id, organisation: info.organisationUnit.id]"/>
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(this).find('a.cluetip').cluetip(cluetipOptions);	
})
</script>
