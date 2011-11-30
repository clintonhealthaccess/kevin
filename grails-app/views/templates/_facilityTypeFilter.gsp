<g:form class="filter facility-type" method="get" url="[controller:controllerName, action:actionName]">
	<g:each in="${linkParams}" var="param">
		<g:if test="${param.key != 'action' && param.key != 'controller' && param.key != 'groupUuids'}">
			<input type="hidden" name="${param.key}" value="${param.value}"/>
		</g:if>
	</g:each>
	<h4 class="bold"><g:message code="filter.facility.type.label"/></h4>
	<div id="facility-type-filter">
		<g:if test="${!facilityTypes.isEmpty()}">
			<ul class="horizontal">
				<g:each in="${facilityTypes}" var="group">
					<li><input type="checkbox" name="groupUuids" value="${group.uuid}" ${currentFacilityTypes.contains(group)?'checked="checked"':'""'}/>${group.name}</li>
				</g:each>
			</ul>
		</g:if>
		<g:else>
			<span class="italic"><g:message code="filter.facility.type.empty.label"/></span>
		</g:else>
	</div>
	<button type="submit">Filter</button>
</g:form>