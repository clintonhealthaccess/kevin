<div class="entity-form-container togglable">
	
	<div class="entity-form-header">
		<h3 class="title">Dashboard objective</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'dashboardObjective', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="entry.names" label="Name" bean="${objectiveEntry?.entry}" value="${objectiveEntry?.entry.names}" field="names"/>
		<g:i18nTextarea name="entry.descriptions" label="Description" bean="${objectiveEntry?.entry}" value="${objectiveEntry?.entry.descriptions}" field="descriptions"/>
		<g:input name="entry.code" label="Code" bean="${objectiveEntry?.entry}" field="code"/>

		<g:if test="${currentObjective != null}">
			<input type="hidden" name="currentObjective" value="${currentObjective.id}"></input>
		</g:if>
		<g:else>
			<input type="hidden" name="entry.id" value="${objectiveEntry.entry.id}"></input>
			<input type="hidden" name="id" value="${objectiveEntry.id}"></input>
		</g:else>
		
		<g:input name="weight" label="Weight" bean="${objectiveEntry}" field="weight"/>
		<g:input name="order" label="Order" bean="${objectiveEntry}" field="order"/>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>