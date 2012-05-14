<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planning.planningcost.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'planningCost', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="planningType.id" value="${planningCost.planningType.id}"/>
	
		<g:i18nInput name="names" bean="${planningCost}" value="${planningCost.names}" label="${message(code:'entity.name.label')}" field="names"/>

		<g:if test="${enume != null}">
			<g:selectFromList name="discriminatorValues" label="${message(code:'planning.planningcost.discriminatorvalue.label')}" bean="${planningCost}" field="discriminatorValueString" multiple="true"
				from="${enume.activeEnumOptions*.value}" value="${planningCost.discriminatorValues}"/>
		</g:if>
		<g:else>
			<g:input name="discriminatorValues" label="${message(code:'planning.planningcost.discriminatorvalue.label')}" field="discriminatorValueString" 
				value="${planningCost.discriminatorValueString}" bean="${planningCost}"/> 
		</g:else>
		
		<g:selectFromList name="dataElement.id" label="${message(code:'planning.planningcost.dataelement.label')}" bean="${planningCost}" field="dataElement" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'NormalizedDataElement'])}"
			from="${dataElements}" value="${planningCost.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<g:selectFromEnum name="type" bean="${planningCost}" values="${PlanningCostType.values()}" field="type" label="${message(code:'planning.planningcost.type.label')}"/>
	
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${target}" field="order"/>
	
		<g:if test="${planningCost.id != null}">
			<input type="hidden" name="id" value="${planningCost.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>