<div id="add-dsr-objective" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">D.S.Rs Objective</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'dsrObjective', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${objective}" value="${objective.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${objective}" value="${objective.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${objective}" field="code"/>
		<g:input name="order" label="Order" bean="${objective}" field="order"/>
	
		<g:if test="${objective?.id != null}">
			<input type="hidden" name="id" value="${objective.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save Objective</button>&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>