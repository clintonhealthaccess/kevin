<ul class="horizontal" id="tab-subnav">
	<li>
		<a class="${selected=='overview'?'selected':''}" href="${createLink(controller:'planning', action:'overview', params:[planning: planning.id, location: location.id])}">
			Overview
		</a>
	</li>
	
	<g:each in="${planning.planningTypes}" var="planningType">
		<li>
			<a class="${selected==planningType.id?'selected':''}" href="${createLink(controller:'planning', action:'planningList', params:[planningType:planningType.id, location:location.id])}">
				<g:i18n field="${planningType.names}"/>
			</a>
		</li>
	</g:each>
</ul>