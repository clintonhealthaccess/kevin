<g:each in="${dataElements}" var="dataElement">
	<li data-code="${dataElement.id}" id="data-element-${dataElement.id}">
		<a	class="no-link" onclick="return false;" title="${dataElement.name}"
			href="${createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: dataElement.id])}"
			rel="${createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: dataElement.id])}">
		${dataElement.name}</a> <span>[${dataElement.id}]</span>
	</li>
</g:each>

