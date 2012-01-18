<g:set var="organisationUnitGroup" value="${surveyPage.entity.type}"/>

<div id="question-${question.id}" class="question question-simple" data-question="${question.id}">
	<h4>
		<span class="question-number">${surveyPage.getQuestionNumber(question)}</span><g:i18n field="${question.names}" />
	</h4>

	<g:ifText field="${question.descriptions}">
		<p class="show-question-help"><a href="#">Show tips</a></p>
		<div class="question-help-container">
			<div class="question-help"><g:i18n field="${question.descriptions}"/><a class="hide-question-help">Close tips</a></div>
		</div>
	</g:ifText>
	<div class="clear"></div>
	
    <g:set var="surveyElement" value="${question.surveyElement}"/>
    
	<g:if test="${print && surveyElement?.dataElement.type.type.name().toLowerCase()=='list' && !appendix}">
		<label>-- <g:message code="survey.print.see.appendix" default="See Appendix"/> --</label>
	</g:if>
	<g:else>
		<div id="element-${surveyElement?.id}" class="survey-element">
			<g:if test="${surveyElement != null}">
				<g:each in="${question.steps}" var="step">
					<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
					<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />

					<g:set var="type" value="${question.getType(step)}"/>
					<g:set var="value" value="${question.getValue(enteredValue.value, step)}"/>
					<g:set var="lastValue" value="${question.getValue(enteredValue.lastValue, step)}"/>
				
					<g:render template="/survey/element/${type.name().toLowerCase()}"  model="[
						value: value,
						lastValue: lastValue,
						type: type, 
						suffix: step.prefix,
						surveyElement: surveyElement, 
						enteredValue: enteredValue, 
						readonly: readonly,
						print: print,
						appendix: appendix
					]"/>
				</g:each>
			</g:if>
			<g:else>
				No survey element for this question.
			</g:else>
		</div>
	</g:else>
</div>
