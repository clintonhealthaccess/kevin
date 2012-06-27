<ul class="horizontal tab-navigation">
	<li><a class="${selected=='undertakings'?'selected':''}" href="${createLink(controller:'editPlanning', action:'overview', params:[planning: planning.id, location: location.id])}"><g:message code="planning.tabs.undertakings"/></a></li>
	<li><a class="${selected=='budget'?'selected':''}" href="${createLink(controller:'editPlanning', action:'budget', params:[planning: planning.id, location: location.id])}"><g:message code="planning.tabs.budget"/></a></li>
	<g:each in="${planning.planningOutputs}" var="output">
		<li>
			<a class="${selected=='output-'+output.id?'selected':''}" href="${createLink(controller:'editPlanning', action:'output', params:[planningOutput: output.id, location: location.id])}">
				<g:i18n field="${output.names}"/>
			</a>
		</li>
	</g:each>
</ul>
