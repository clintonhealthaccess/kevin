<g:set var="type" value="${surveyPage.dataLocation.type}"/>

<div id="question-${question.id}" class="question question-simple ${surveyPage.enteredQuestions[question].skipped?'hidden':''} ${!surveyPage.enteredQuestions[question].complete?'incomplete':''} ${surveyPage.enteredQuestions[question].invalid?'invalid':''}" data-question="${question.id}">
	<h4 class="nice-title">
		<span class="nice-title-image">${surveyPage.getQuestionNumber(question)}</span><g:i18n field="${question.names}" />
	</h4>

	<g:ifText field="${i18n(field: question.descriptions)}">
		<g:render template="/templates/help" model="[content: i18n(field: question.descriptions)]"/>
	</g:ifText>
	<div class="clear"></div>
	
    <g:set var="surveyElement" value="${question.surveyElement}"/>
    
	<g:if test="${print && surveyElement?.dataElement.type.type.name().toLowerCase()=='list' && !appendix}">
		<label>-- <g:message code="survey.print.see.appendix"/> --</label>
	</g:if>
	<g:else>
		<div id="element-${surveyElement?.id}" class="survey-element">
			<g:if test="${surveyElement != null}">
				<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
				<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
			
				<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}"  model="[
					location: enteredValue.dataLocation,
					value: enteredValue.value,
					lastValue: enteredValue.lastValue,
					type: dataElement.type, 
					suffix:'',
					element: surveyElement, 
					validatable: enteredValue.validatable, 
					readonly: readonly,
					print: print,
					appendix: appendix,
					enums: surveyPage.enums
				]"/>
			</g:if>
			<g:else>
				No survey element for this question.
			</g:else>
		</div>
	</g:else>
</div>
