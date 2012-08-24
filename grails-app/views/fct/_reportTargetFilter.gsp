<g:if test="${fctTable.targets != null && !fctTable.targets.empty}">
	<g:form name="fctTarget-form" method="get" url="${[controller:controllerName, action:actionName]}">
		<span><g:message code="fct.report.target.selector"/>:</span>
		<select name="fctTarget" onchange="$(document).find('#js_fct-target-'+$(this).val())[0].click();">
			<g:each in="${fctTable.targets}" var="target">
				<option ${target.id == currentTarget?.id ? 'selected="selected"' : ''} value="${target.id}">
					<g:i18n field="${target.names}" />
				</option>
			</g:each>
		</select>
		<g:render template="/templates/help_tooltip" 
			model="[names: i18n(field: currentTarget.names), descriptions: i18n(field: currentTarget.descriptions)]" />
	</g:form>
	<div class="hidden">
		<g:each in="${fctTable.targets}" var="target">
			<%
				newLinkParams = [:]
				newLinkParams.putAll linkParams
				newLinkParams['fctTarget'] = target.id
			%>				
			<a id="js_fct-target-${target.id}" href="${createLink(controller:controllerName, action:actionName, params:newLinkParams)}"></a>
		</g:each>
	</div>
</g:if>
