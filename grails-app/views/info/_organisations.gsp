<ul><g:each in="${organisations}" var="organisation">
	<g:set var="calculationValue" value="${info.getValue(organisation)}"/>
	<g:if test="${calculationValue != null}">
		<li>
			<span>${organisation.name}</span>
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