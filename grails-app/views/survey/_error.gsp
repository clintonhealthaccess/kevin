${message}
<g:if test="${rule.allowOutlier}">
	<a class="outlier-validation" href="#" data-rule="${rule.id}">Yes</a>
	<input type="hidden" name="surveyElements[${surveyElement.id}].acceptedWarnings" value="-1"/>
	<input type="hidden" name="surveyElements[${surveyElement.id}].acceptedWarnings[${rule.id}]" value="${rule}"/>
</g:if>
