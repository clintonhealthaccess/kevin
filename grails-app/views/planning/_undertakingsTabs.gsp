<ul class="horizontal tab-subnav">
	<li>
		<a class="${selected=='overview'?'selected':''}" href="${createLink(controller:'editPlanning', action:'overview', params:[planning: planning.id, location: location.id])}">
			<g:message code="planning.overview.tabs.overview"/>
		</a>
	</li>
	
	<g:each in="${planning.planningTypes}" var="planningType">
		<li>
			<a class="${selected==planningType.id?'selected':''}" href="${createLink(controller:'editPlanning', action:'planningList', params:[planningType:planningType.id, location:location.id])}">
				<g:i18n field="${planningType.names}"/>
			</a>
		</li>
	</g:each>
</ul>