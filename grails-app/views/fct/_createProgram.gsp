<div id="add-dsr-program" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Fct Program</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'fctProgram', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${program}" value="${program.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${program}" value="${program.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${program}" field="code"/>
		<g:input name="order" label="Order" bean="${program}" field="order"/>
	
		<g:if test="${program?.id != null}">
			<input type="hidden" name="id" value="${program.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save Program</button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>