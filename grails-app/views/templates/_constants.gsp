<g:each in="${constants}" var="constant">
	<li data-code="c${constant.id}" id="constant-${constant.id}">
		<a class="no-link cluetip" onclick="return false;" title="${i18n(field: constant.names)}"
			href="${createLink(controller: params['controller'], action:'getConstantDescription', params:[constant: constant.id])}"
			rel="${createLink(controller: params['controller'], action:'getConstantDescription', params:[constant: constant.id])}">
		<g:i18n field="${constant.names}"/></a> <span>[c${constant.id}]</span>
	</li>
</g:each>

