<div>
	<g:i18n field="${question.names}" />
	<ul>
		<g:each in="${question.getOptions(organisationUnitGroup)}" var="option">
			<g:set var="surveyElement" value="${option.surveyElement}"/>
			<g:set var="dataElement" value="${surveyElement.dataElement}"/>
		    <li>
				<g:render template="/survey/element/${dataElement.type}" model="[surveyElementValue: surveyElementValues[surveyElement.id], readonly: readonly]"/>
				<span><g:i18n field="${option.names}"/></span>
			</li>
		</g:each>
	</ul>		
</div>