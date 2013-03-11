<g:if test="${!indicators.empty}">
	<g:form name="indicator-form" method="get" url="${[controller:controllerName, action:actionName]}">
		<span><g:message code="report.indicator.selector"/>:</span>
		<select name="indicator" onchange="$(document).find('#js_indicator-'+$(this).val())[0].click();">
			<g:if test="${currentIndicator == null ? 'selected="selected"' : ''}">
				<option>
					<g:i18n field="${program.names}" />
				</option>
			</g:if>
			<g:each in="${indicators}" var="indicator">
				<option ${indicator.id == currentIndicator?.id ? 'selected="selected"' : ''} value="${indicator.id}">
					<g:i18n field="${indicator.names}" />
				</option>
			</g:each>
		</select>
		<g:if test="${currentIndicator != null}">
			<g:render template="/templates/help_tooltip" 
				model="[names: i18n(field: currentIndicator.names), descriptions: i18n(field: currentIndicator.descriptions)]" />
		</g:if>
	</g:form>
	<div class="hidden">
		<g:each in="${indicators}" var="indicator">
			<%
				reportIndicatorLinkParams = [:]
				reportIndicatorLinkParams.putAll linkParams
				reportIndicatorLinkParams[indicatorParam] = indicator.id
			%>				
			<a id="js_indicator-${indicator.id}" href="${createLink(controller:controllerName, action:actionName, params:reportIndicatorLinkParams)}"></a>
		</g:each>
	</div>
</g:if>
