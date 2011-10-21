<div class="filter facility-type">
	<h4 class="bold"><g:message code="filter.facility.type.label"/></h4>
	<div id="facility-type-filter">
		<g:if test="${!facilityTypes.isEmpty()}">
			<ul>
				<g:each in="${facilityTypes}" var="group">
					<li><input type="checkbox" value="${group.uuid}" ${checkedFacilities.contains(group.uuid)?'checked="checked"':'""'}/>${group.name}</li>
				</g:each>
			</ul>
		</g:if>
		<g:else>
			<span class="italic"><g:message code="filter.facility.type.empty.label"/></span>
		</g:else>
	</div>
</div>