<div class="entity-form-container">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'maps.target.label')]"/>
		</h3>
		<g:locales/>
	</div>

	<g:form url="[controller:'mapsTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="${message(code:'entity.name.label')}" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="${message(code:'entity.description.label')}" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
	
		<g:selectFromList name="calculation.id" label="${message(code:'maps.calculation.label')}" bean="${target?.calculation}" field="calculation" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[classes:['Sum', 'Aggregation']])}"
			from="${calculations}" value="${target?.calculation?.id}" values="${calculations.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${target}" field="order"/>
		
		<g:if test="${target?.id != null}">
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
</div>