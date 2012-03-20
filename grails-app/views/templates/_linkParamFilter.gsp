<g:each in="${linkParams}" var="param">
	<g:if test="${param.key != 'controller' && param.key != 'action' && !exclude?.contains(param.key)}">
		<g:each in="${param.value}" var="value">
			<input type="hidden" name="${param.key}" value="${value}"/>
		</g:each>
	</g:if>
</g:each>