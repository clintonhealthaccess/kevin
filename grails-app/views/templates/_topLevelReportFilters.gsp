<g:form name="report-filters" method="get" url="[controller:controllerName, action:actionName]">
<<<<<<< HEAD
<g:programFilter linkParams="${params}" selected="${currentProgram}" selectedTarget="${currentTarget}"/>
<g:locationFilter linkParams="${params}" selected="${currentLocation}" selectedTypes="${currentLocationTypes}"/>
=======
<g:programFilter linkParams="${params}" selected="${currentObjective}" selectedTarget="${currentTarget}"/>
<g:locationFilter linkParams="${params}" selected="${currentLocation}" selectedTypes="${currentLocationTypes}" skipLevels="${skipLevels}"/>
>>>>>>> master
<g:iterationFilter linkParams="${params}" selected="${currentPeriod}"/>
<g:locationTypeFilter linkParams="${params}" selected="${currentLocationTypes}" />
</g:form>