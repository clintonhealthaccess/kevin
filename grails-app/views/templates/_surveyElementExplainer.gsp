<g:set var="question" value="${surveyElement.surveyQuestion}"/>

<div class="row">
   <span class="type"> 
        Survey: 
   </span> 
   <g:i18n field="${question.section.objective.survey.names}"/>
        [ ${question.section.objective.survey.period.startDate} &harr; 
        ${question.section.objective.survey.period.endDate} ]
</div>
<div class="row">
   <span class="type"> 
    Objective: 
    </span>
        <g:i18n field="${question.section.objective.names}"/>
</div>
<div class="row">
   <span class="type"> 
     Section:
   </span> 
     <g:i18n field="${question.section.names}"/>
</div>

<div class="row">
   <span class="type"> 
     Data Element:
   </span> 
         <g:i18n field="${surveyElement.dataElement.names}"/>
</div>
<div>
   <span class="type"> 
    Question: 
   </span>
   <span class="display-in-block"> 
    <g:i18n field="${question.names}"/>
    </span>
</div>


