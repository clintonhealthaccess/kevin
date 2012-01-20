<ul><g:each in="${locations}" var="location">
	<g:set var="calculationValue" value="${info.getValue(location)}"/>
	<g:if test="${calculationValue != null}">
		<li>
			<span>${location.name}</span>
			<span class="bold">
				<g:if test="${calculationValue.value?.numberValue != null}">
					<g:formatNumber number="${calculationValue.value?.numberValue * 100}" format="#0.0"/>%
				</g:if>
				<g:else>
					N/A
				</g:else>
			</span>
		</li>
	</g:if>
</g:each></ul>