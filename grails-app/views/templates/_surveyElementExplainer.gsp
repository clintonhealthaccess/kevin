<g:set var="question" value="${surveyElement.surveyQuestion}"/>

<div class="row">
   <span class="type"> 
        <g:message code="general.text.survey" default="Survey"/>: 
   </span> 
   <g:i18n field="${question.section.objective.survey.names}"/>
        [ ${question.section.objective.survey.period.startDate} &harr; 
        ${question.section.objective.survey.period.endDate} ]
</div>
<div class="row">
	<span class="type"><g:message code="general.text.objective" default="Objective"/>:</span>
    <g:i18n field="${question.section.objective.names}"/>
</div>
<div class="row">
	<span class="type"><g:message code="general.text.section" default="Section"/>:</span> 
	<g:i18n field="${question.section.names}"/>
</div>

<div class="row">
	<span class="type"><g:message code="survey.dataelement.label" default="Data Element"/>:</span> 
	<g:i18n field="${surveyElement.dataElement.names}"/>
</div>
<div>
	<span class="type"><g:message code="general.text.question" default="Question"/>:</span>
	<div> 
		<g:i18n field="${question.names}"/>
    </div>
</div>


