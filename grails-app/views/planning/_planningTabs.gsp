<ul class="horizontal tab-navigation">
	<li><a class="${selected=='undertakings'?'selected':''}" href="${createLink(controller:'editPlanning', action:'overview', params:[planning: planning.id, location: location.id])}"><g:message code="planning.tabs.undertakings"/></a></li>
	<li><a class="${selected=='budget'?'selected':''}" href="${createLink(controller:'editPlanning', action:'budget', params:[planning: planning.id, location: location.id])}"><g:message code="planning.tabs.budget"/></a></li>
	<li class="settings"><a href="#"><g:message code="planning.tabs.settings"/></a></li>
</ul>
