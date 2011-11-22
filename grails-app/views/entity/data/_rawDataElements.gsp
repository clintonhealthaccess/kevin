<g:each in="${rawDataElements}" status="i" var="rawDataElement">
	<li data-code="${rawDataElement.id}" id="data-element-${rawDataElement.id}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
		<a	class="no-link cluetip" onclick="return false;" title="${i18n(field:rawDataElement.names)}"
			href="${createLink(controller: 'rawDataElement', action:'getDescription', params:[rawDataElement: rawDataElement.id])}"
			rel="${createLink(controller: 'rawDataElement', action:'getDescription', params:[rawDataElement: rawDataElement.id])}">
		<g:i18n field="${rawDataElement.names}"/></a> <span>[${rawDataElement.id}]</span>
	</li>
</g:each>

