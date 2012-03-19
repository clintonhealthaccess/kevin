<div class="entity-form-container togglable">
	
	<div class="entity-form-header">
		<h3 class="title">Dashboard program</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'dashboardProgram', action:'save', params:[targetURI:targetURI]]" useToken="true">		

		<g:selectFromList name="program.id" label="Program" bean="${entity}" field="program" optionKey="id" multiple="false"
			from="${programs}" value="${entity.program?.id}" values="${programs.collect{i18n(field:it.names)}}" />
		
		<g:input name="weight" label="Weight" bean="${entity}" field="weight"/>
		<g:input name="order" label="Order" bean="${entity}" field="order"/>
		
		<g:if test="${entity.id != null}">
			<input type="hidden" name="id" value="${entity.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>