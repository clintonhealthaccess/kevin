<g:form name="report-filters" method="get" url="[controller:controllerName, action:actionName]">
<g:programFilter linkParams="${params}" selected="${currentObjective}" selectedTarget="${currentTarget}"/>
<g:locationFilter linkParams="${params}" selected="${currentLocation}" selectedTypes="${currentLocationTypes}"/>
<g:iterationFilter linkParams="${params}" selected="${currentPeriod}"/>
<g:locationTypeFilter linkParams="${params}" selected="${currentLocationTypes}" />
</g:form>