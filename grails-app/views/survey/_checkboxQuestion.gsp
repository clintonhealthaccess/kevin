<g:i18n field="${question.names}" />
<span> 
	<ul>
		<g:each in="${question.options}" var="option">
			<g:set var="surveyElement" value="${option.surveyElement}"/>
			<g:set var="dataElement" value="${surveyElement.dataElement}"/>
		    <li>
			   <g:render template="/survey/${dataElement.type}" model="[surveyElementValue: surveyElementValues[surveyElement.id]]" />
			   <g:i18n field="${option.names}"/>
		    </li>
		</g:each>
	</ul>		
</span>