<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-checkbox" data-question="${question.id}">
	<h4><span class="question-number">1</span><g:i18n field="${question.names}" /></h4>
        <div class="question-help-container">
          <p class='show_question_help'><a href="#">Show tips</a></p>
          <p class="question-help"><g:i18n field="${question.descriptions}"/><a class='hide_question_help'>Close tips</a></p>
        </div>
	<ul class="clear">
		<g:each in="${question.getOptions(organisationUnitGroup)}" var="option">
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
					<g:i18n field="${option.names}"/></span></span>
				</g:if>
				<g:else>
					No survey element for this option.
				</g:else>
				<div class="clear"></div>
			</li>
		</g:each>
	</ul>
</div>
