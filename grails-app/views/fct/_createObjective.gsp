<div id="add-dsr-objective" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Fct Objective</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'fctObjective', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${objective}" value="${objective.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${objective}" value="${objective.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${objective}" field="code"/>
		<g:input name="order" label="Order" bean="${objective}" field="order"/>
	
		<g:if test="${objective?.id != null}">
			<input type="hidden" name="id" value="${objective.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save Objective</button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>