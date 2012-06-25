<div class="selector">
	<g:if test="${dsrTable.targetCategories != null && !dsrTable.targetCategories.empty}">
		<g:form name="dsrCategory-form" method="get" url="${[controller:'dsr', action:'view']}">		
			
			<span><g:message code="dsr.report.category.selector"/>:</span>
			<select name="dsrCategory" onchange="$(document).find('#js_dsr-category-'+$(this).val())[0].click();">
				<g:each in="${dsrTable.targetCategories}" var="category">
					<option ${category.id == currentCategory?.id ? 'selected="selected"' : ''} value="${category.id}">
						<g:i18n field="${category.names}" />
					</option>
				</g:each>
			</select>
			<g:render template="/templates/help_tooltip" 
				model="[names: i18n(field: currentCategory.names), descriptions: i18n(field: currentCategory.descriptions)]" />			
		</g:form>
		<div class="hidden">
			<g:each in="${dsrTable.targetCategories}" var="category">
				<%
					newLinkParams = [:]
					newLinkParams.putAll linkParams
					newLinkParams['dsrCategory'] = category.id
				%>
				
				<a id="js_dsr-category-${category.id}" href="${createLink(controller: 'dsr', action: 'view', params: newLinkParams)}"></a>
			</g:each>
		</div>
	</g:if>
</div>