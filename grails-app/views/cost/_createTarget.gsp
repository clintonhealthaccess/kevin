<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Costing target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'costTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>

		<g:selectFromList name="dataElement.id" label="Data element" bean="${target}" field="dataElement" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'DataElement'])}"
			from="${dataElements}" value="${target.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />

		<g:selectFromList name="dataElementEnd.id" label="Data element end" bean="${target}" field="dataElementEnd" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'DataElement'])}"
			from="${dataElementsEnd}" value="${target.dataElementEnd?.id}" values="${dataElementsEnd.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />

		<g:selectFromList name="objective.id" label="Objective" bean="${target}" field="objective" optionKey="id" multiple="false"
			from="${objectives}" value="${target.objective?.id}" values="${objectives.collect{i18n(field:it.names)}}" />
	
		<g:selectFromList name="typeCodes" label="${message(code:'facility.type.label')}" bean="${target}" field="typeCodeString" 
			from="${types}" value="${target.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:selectFromList name="costRampUp.id" label="Cost Ramp Up" bean="${target}" field="costRampUp" optionKey="id" multiple="false"
			from="${costRampUps}" value="${target.costRampUp?.id}" values="${costRampUps.collect{i18n(field:it.names)}}" />
	
		<g:selectFromEnum name="costType" bean="${target}" values="${CostType.values()}" field="costType" label="Type"/>
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		
		<g:if test="${currentObjective != null}">
			<input type="hidden" name="currentObjective" value="${currentObjective.id}"></input>
		</g:if>
		<g:else>
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:else>
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
