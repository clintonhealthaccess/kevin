<div class="selector">
	<g:if test="${fctTargets != null && !fctTargets.empty}">
		<g:form name="fctTarget-form" method="get" url="${[controller:'fct', action:'view']}">
			
			<g:linkParamFilter linkParams="${linkParams}" exclude="${['fctTarget']}" />
			
			<span>Report Type:</span>
			<select id="fctTarget" name="fctTarget">
				<g:each in="${fctTargets}" var="target">
					<option ${target.id == currentTarget?.id ? 'selected="selected"' : ''} value="${target.id}">
						<g:i18n field="${target.names}" />
					</option>
				</g:each>
			</select>
		</g:form>
	</g:if>
</div>