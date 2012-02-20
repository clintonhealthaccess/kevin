<g:render template="/templates/programFilter" model="[params:params]" />
<g:locationFilter linkParams="${params}" selected="${currentLocation}"/>
<g:iterationFilter linkParams="${params}" selected="${currentPeriod}"/>
<g:locationTypeFilter linkParams="${params}" selected="${currentLocationTypes}" />
