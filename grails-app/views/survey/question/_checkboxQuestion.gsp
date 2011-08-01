<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div class="question question-checkbox question-${question.id} ${surveyPage.isValid(question)?'':'errors'}" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	<ul>
		<g:each in="${question.getOptions(organisationUnitGroup)}" var="option">
			<g:set var="surveyElement" value="${option.surveyElement}"/>
			<g:set var="dataElement" value="${surveyElement.dataElement}"/>
			<g:set var="surveyEnteredValue" value="${surveyPage.enteredValues[surveyElement]}"/>
			
		    <li class="element element-${surveyElement.id} ${surveyEnteredValue.skipped?'skipped':''} ${!surveyEnteredValue.valid?'errors':''}">
				<g:render template="/survey/element/${dataElement.type}" model="[surveyElement: surveyElement, surveyPage: surveyPage, readonly: readonly]"/>
				<span><g:i18n field="${option.names}"/></span>
				<div class="clear"></div>
			</li>
		</g:each>
	</ul>
</div>