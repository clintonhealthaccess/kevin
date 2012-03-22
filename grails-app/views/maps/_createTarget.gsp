<div id="add-maps-target" class="entity-form-container">

	<div class="entity-form-header">
		<h3 class="title">Maps target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'mapsTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
	
		<g:selectFromList name="calculation.id" label="Calculation" bean="${target?.calculation}" field="calculation" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[classes:['Average', 'Aggregation']])}"
			from="${calculations}" value="${target?.calculation?.id}" values="${calculations.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		
		<g:if test="${target?.id != null}">
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
</div>