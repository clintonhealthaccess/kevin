<ul><g:each in="${expressionValues}" var="entry">
	<g:set var="organisation" value="${entry.key}"/>
	<g:set var="expressionValue" value="${entry.value}"/>
	<g:if test="${expressionValue != null}">
		<li>
			<span>${organisation.name}</span>
			<span class="bold">
				<g:if test="${expressionValue.numberValue != null}">
					<g:formatNumber number="${expressionValue.numberValue * 100}" format="#0.0"/>%
				</g:if>
				<g:else>
					N/A
				</g:else>
			</span>
		</li>
	</g:if>
</g:each></ul>