<g:each in="${dataElements}" status="i" var="dataElement">
	<li data-code="${dataElement.id}" id="data-element-${dataElement.id}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
		<a	class="no-link cluetip" onclick="return false;" title="${i18n(field:dataElement.names)}"
			href="${createLink(controller: params['controller'], action:'getDataElementDescription', params:[dataElement: dataElement.id])}"
			rel="${createLink(controller: params['controller'], action:'getDataElementDescription', params:[dataElement: dataElement.id])}">
		<g:i18n field="${dataElement.names}"/></a> <span>[${dataElement.id}]</span>
	</li>
</g:each>

