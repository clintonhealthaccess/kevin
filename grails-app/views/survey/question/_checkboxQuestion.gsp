<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-checkbox" data-question="${question.id}">
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

	<g:if test="${print}">
		<label>-- <g:message code="survey.print.selectallthatapply.label" default="Select all that apply"/> --</label>
	</g:if>
	
	<ul>
		<g:each in="${surveyPage.getOptions(question)}" var="option">
			<g:set var="surveyElement" value="${option.surveyElement}"/>

		    <li id="element-${surveyElement?.id}" class="survey-element">
		    	<g:if test="${surveyElement != null}">
					<g:set var="dataElement" value="${surveyElement.dataElement}"/>
					<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
				
					<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
						value: enteredValue.value, 
						lastValue: enteredValue.lastValue,
						type: dataElement.type, 
						suffix:'',
						surveyElement: surveyElement, 
						enteredValue: enteredValue, 
						readonly: readonly,
						isCheckbox: true
					]"/>
					<g:i18n field="${option.names}"/>
				</g:if>
				<g:else>
					No survey element for this option.
				</g:else>
				<div class="clear"></div>
			</li>
		</g:each>
	</ul>
</div>
