<g:set var="question" value="${surveyElement.surveyQuestion}"/>

<div class="row">
   <span class="type"> 
        <g:message code="survey.label"/>: 
   </span> 
   <g:i18n field="${question.section.program.survey.names}"/>
        [ ${question.section.program.survey.period.startDate} &harr; 
        ${question.section.program.survey.period.endDate} ]
</div>
<div class="row">
	<span class="type"><g:message code="survey.program.label"/>:</span>
    <g:i18n field="${question.section.program.names}"/>
</div>
<div class="row">
	<span class="type"><g:message code="survey.section.label"/>:</span> 
	<g:i18n field="${question.section.names}"/>
</div>

<div>
	<span class="type"><g:message code="survey.question.label"/>:</span>
	<div> 
		<g:i18n field="${question.names}"/>
    </div>
</div>

<div class="row">
	<span class="type"><g:message code="dataelement.label"/>:</span> 
	<g:i18n field="${surveyElement.dataElement.names}"/>
</div>

<g:render template="/entity/data/dataElementDescription" model="${[dataElement: surveyElement.dataElement]}"/>


