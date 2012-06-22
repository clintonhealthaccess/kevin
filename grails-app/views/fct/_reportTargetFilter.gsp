<div class="selector">
	<g:if test="${fctTable.targets != null && !fctTable.targets.empty}">
		<g:form name="fctTarget-form" method="get" url="${[controller:'fct', action:'view']}">
			
			<g:linkParamFilter linkParams="${linkParams}" exclude="${['fctTarget']}" />
			
			<span><g:message code="fct.report.target.selector"/>:</span>
			<select name="fctTarget" onchange="$(this).parents('form').submit();">
				<g:each in="${fctTable.targets}" var="target">
					<option ${target.id == currentTarget?.id ? 'selected="selected"' : ''} value="${target.id}">
						<g:i18n field="${target.names}" />
					</option>
				</g:each>
			</select>
			<g:render template="/templates/help_tooltip" 
				model="[names: i18n(field: currentTarget.names), descriptions: i18n(field: currentTarget.descriptions)]" />
		</g:form>
	</g:if>
</div>
