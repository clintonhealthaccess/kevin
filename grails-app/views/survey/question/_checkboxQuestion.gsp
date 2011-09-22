<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-checkbox" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	<ul>
		<g:each in="${question.getOptions(organisationUnitGroup)}" var="option">
			<g:set var="surveyElement" value="${option.surveyElement}"/>
			<g:set var="dataElement" value="${surveyElement.dataElement}"/>
							
			<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />

			<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
			<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
		    <li id="element-${surveyElement.id}" class="survey-element">
				<span class="display-in-block"><g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
					value: enteredValue.value, 
					lastValue: enteredValue.lastValue,
					type: dataElement.type, 
					suffix:'',
					surveyElement: surveyElement, 
					enteredValue: enteredValue, 
					readonly: readonly
				]"/>
				<span><g:i18n field="${option.names}"/></span></span>
				<div class="clear"></div>
			</li>
		</g:each>
	</ul>
</div>