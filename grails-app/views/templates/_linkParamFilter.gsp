<g:each in="${linkParams}" var="param">
	<g:if test="${param.key != 'action' && param.key != 'controller' && param.key != filter}">
		<input type="hidden" name="${param.key}" value="${param.value}"/>
	</g:if>
</g:each>