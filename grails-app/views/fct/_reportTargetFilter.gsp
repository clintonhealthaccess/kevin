<g:if test="${fctTargets != null && !fctTargets.empty}">
	<g:form name="fctTarget-form" method="get" url="${[controller:'fct', action:'view']}">
		
		<g:render template="/templates/linkParamFilter" model="[linkParams:linkParams, filter:'fctTarget']" />
		
		<span>Report Type:</span>
		<select id="fctTarget" name="fctTarget">
			<g:each in="${fctTargets}" var="target">
				<option ${target.id == currentFctTarget?.id ? 'selected="selected"' : ''} value="${target.id}">
					<g:i18n field="${target.names}" />
				</option>
			</g:each>
		</select>
	</g:form>
</g:if>