<g:form method="get" url="[controller:controllerName, action:actionName]">
<g:render template="/templates/programFilter"
		model="[linkParams:linkParams]" />
<g:render template="/templates/organisationFilter"
		model="[linkParams:linkParams]" />
<g:render template="/templates/iterationFilter"
		model="[linkParams:linkParams]" />
<g:render template="/templates/facilityTypeFilter"
		model="[linkParams:linkParams]" />
</g:form>