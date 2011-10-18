<ul>
	<g:each in="${errors}" var="error">
		<g:if test="${!error.accepted}">
			<g:if test="${error.displayed}">
				<li>
				${error.message}
				<g:if test="${error.rule.allowOutlier}">
					Please double check it. If it is correct, confirm by clicking <a class="outlier-validation" href="#" data-rule="${error.rule.id}">here</a>.
					<input type="hidden" class="input" name="surveyElements[${surveyElement.id}].value${error.suffix}[warning]" value=""/>
				</g:if>
				</li>
			</g:if>
		</g:if>
		<g:else>
			<input type="hidden" class="input" name="surveyElements[${surveyElement.id}].value${error.suffix}[warning]" value="${error.rule.id}"/>
		</g:else>
	</g:each>
</ul>

