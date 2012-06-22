<div class="selector">
	<g:if test="${dsrTable.targetCategories != null && !dsrTable.targetCategories.empty}">
		<g:form name="dsrCategory-form" method="get" url="${[controller:'dsr', action:'view']}">		
			
			<g:linkParamFilter linkParams="${linkParams}" exclude="${['dsrCategory']}" />
			
			<span><g:message code="dsr.report.category.selector"/>:</span>
			<select name="dsrCategory" onchange="$(this).parents('form').submit();">
				<g:each in="${dsrTable.targetCategories}" var="category">
					<option ${category.id == currentCategory?.id ? 'selected="selected"' : ''} value="${category.id}">
						<g:i18n field="${category.names}" />
					</option>
				</g:each>
			</select>
		</g:form>
	</g:if>
</div>