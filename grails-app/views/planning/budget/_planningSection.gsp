<div class="budget-edit">
	<div class="diff-title">
		<h5>
			<g:value value="${planningEntry.discriminatorValue}" type="${planningType.discriminatorType}" enums="${planningEntry.enums}"/>
		</h5>
		<h6>
			<g:i18n field="${planningType.formElement.headers[section]}"/>
		</h6>
	</div>
	
	<g:form url="[controller:'editPlanning', action:'save', params: [location: location.id, planningType: planningType.id]]">
		<input class="js_always-send" type="hidden" name="lineNumber" value="${planningEntry.lineNumber}"/>
		<input class="js_always-send" type="hidden" name="planningType" value="${planningType.id}"/>
		
		<div id="element-${planningType.formElement.id}">
		<g:render template="/survey/element/${planningType.getType(section).type.name().toLowerCase()}"  model="[
			value: planningEntry.getValue(section),
			lastValue: null,
			type: planningType.getType(section), 
			suffix: planningEntry.getPrefix(section),
			headerSuffix: section,
			
			// get rid of those in the templates??
			element: planningType.formElement,
			validatable: planningEntry.validatable,
			
			readonly: readonly,
			enums: planningEntry.enums
		]"/>
		</div>
	
		<a class="next gray medium" href="${createLink(controller:'editPlanning', action:'updateBudget', params:[location: location.id, planningType: planningType.id])}"><g:message code="planning.budget.panel.update"/></a>
	</g:form>
</div>
