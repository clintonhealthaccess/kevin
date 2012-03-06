<div class="entity-form-container togglable" id="add-dashboard-target">
	
	<div class="entity-form-header">
		<h3 class="title">Dashboard target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'dashboardTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" label="Name" bean="${entity}" value="${entity.names}" field="names"/>
		<g:i18nTextarea name="descriptions" label="Description" bean="${entity}" value="${entity.descriptions}" field="descriptions"/>
		<g:input name="code" label="Code" bean="${entity}" field="code"/>
		
		<g:selectFromList name="program.id" label="Program" bean="${entity}" field="program" optionKey="id" multiple="false"
			from="${programs}" value="${entity.program?.id}" values="${programs.collect{i18n(field:it.names)}}" />
		
		<g:selectFromList name="calculation.id" label="Calculation" bean="${entity}" field="calculation" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[classes:['Average', 'Aggregation']])}"
			from="${calculations}" value="${entity?.calculation?.id}" values="${calculations.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
				
		<g:input name="weight" label="Weight" bean="${entity}" field="weight"/>
		<g:input name="order" label="Order" bean="${entity}" field="order"/>
		
		<input type="hidden" name="id" value="${entity.id}"></input>
			
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
