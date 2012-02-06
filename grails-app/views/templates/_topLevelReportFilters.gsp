<g:form method="get" url="[controller:controllerName, action:actionName]">
<g:render template="/templates/programFilter"
		model="[params:params]" />
<g:render template="/templates/locationFilter"
		model="[params:params]" />
<g:render template="/templates/iterationFilter"
		model="[params:params]" />
<g:render template="/templates/facilityTypeFilter"
		model="[params:params]" />
</g:form>