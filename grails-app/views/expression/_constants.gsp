<g:each in="${constants}" var="constant">
	<li data-code="c${constant.id}" id="constant-${constant.id}">
		<a class="no-link" onclick="return false;" title="${constant.name}"
			href="${createLink(controller:'expression', action:'getConstantDescription', params:[constant: constant.id])}"
			rel="${createLink(controller:'expression', action:'getConstantDescription', params:[constant: constant.id])}">
		${constant.name}</a> <span>[c${constant.id}]</span>
	</li>
</g:each>

