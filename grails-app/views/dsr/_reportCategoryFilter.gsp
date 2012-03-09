<div class="selector">
	<g:if test="${dsrTable.targetCategories != null && !dsrTable.targetCategories.empty}">
	<g:form name="dsrCategory-form" method="get"
		url="${[controller:'dsr', action:'view',  
		params:[period:currentPeriod.id, program:currentProgram.id, location:currentLocation.id, locationTypes:currentLocationTypes*.id]]}">		
		<span>Report Category:</span>
		<select id="dsrCategory" name="dsrCategory">
			<g:each in="${dsrTable.targetCategories}" var="category">
				<option ${category.id == currentCategory?.id ? 'selected="selected"' : ''} value="${category.id}">
					<g:i18n field="${category.names}" />
				</option>
			</g:each>
		</select>
	</g:form>
	</g:if>
</div>