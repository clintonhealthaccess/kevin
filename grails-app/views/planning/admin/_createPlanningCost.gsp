<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planningCost.label',default:'Planning')]"/>
		</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'planningCost', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="planningType.id" value="${planningCost.planningType.id}"/>
	
		<g:i18nInput name="names" bean="${planningCost}" value="${planningCost.names}" label="Name" field="names"/>

		<g:if test="${enume != null}">
			<g:selectFromList name="discriminatorValues" label="Discriminator Value" bean="${planningCost}" field="discriminatorValueString" multiple="true"
				from="${enume.activeEnumOptions*.value}" value="${planningCost.discriminatorValues}"/>
		</g:if>
		<g:else>
			<g:input name="discriminatorValues" label="Discriminator Value - comma-separated" field="discriminatorValueString" 
				value="${planningCost.discriminatorValueString}" bean="${planningCost}"/> 
		</g:else>
		
		<g:selectFromList name="dataElement.id" label="Data Element" bean="${planningCost}" field="dataElement" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'NormalizedDataElement'])}"
			from="${dataElements}" value="${planningCost.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<g:selectFromList name="section" label="Section (link)" bean="${planningCost}" field="section" multiple="false"
			from="${sections}" value="${planningCost.section}"/>
		<g:selectFromList name="groupSection" label="Section (link)" bean="${planningCost}" field="groupSection" multiple="false"
			from="${sections}" value="${planningCost.groupSection}"/>
			
		<g:selectFromEnum name="type" bean="${planningCost}" values="${PlanningCostType.values()}" field="type" label="Type"/>
	
		<g:if test="${planningCost.id != null}">
			<input type="hidden" name="id" value="${planningCost.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>