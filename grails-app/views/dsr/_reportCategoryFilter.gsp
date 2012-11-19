<div class="selector">
	<g:if test="${!targetCategories.empty}">
		<g:form name="dsrCategory-form" method="get" url="${[controller:controllerName, action:actionName]}">			
			<span><g:message code="dsr.report.category.selector"/>:</span>
			<select name="dsrCategory" onchange="$(document).find('#js_dsr-category-'+$(this).val())[0].click();">
				<g:each in="${targetCategories}" var="category">
					<option ${category.id == currentCategory?.id ? 'selected="selected"' : ''} value="${category.id}">
						<g:i18n field="${category.names}" />
					</option>
				</g:each>
			</select>
			<g:render template="/templates/help_tooltip" 
				model="[names: i18n(field: currentCategory.names), descriptions: i18n(field: currentCategory.descriptions)]" />			
		</g:form>
		<div class="hidden">
			<g:each in="${targetCategories}" var="category">
				<%
					newLinkParams = [:]
					newLinkParams.putAll linkParams
					newLinkParams['dsrCategory'] = category.id
				%>
				<a id="js_dsr-category-${category.id}" href="${createLink(controller:controllerName, action:actionName, params:newLinkParams)}"></a>
			</g:each>
		</div>
	</g:if>
	<g:if test="${currentIndicators != null && !currentIndicators.empty}">
		<div class="indicators-selected">
			<g:message code="dsr.report.target.selected"/>:
			<span>
				<g:each in="${currentIndicators}" var="indicator" status="i">						
					<g:i18n field="${indicator.names}" />
					<g:render template="/templates/help_tooltip" 
					model="[names: i18n(field: indicator.names), descriptions: i18n(field: indicator.descriptions)]" />
					<g:if test="${i != currentIndicators.size()-1}">, </g:if>
				</g:each>
			</span>
		</div>
	</g:if>
</div>