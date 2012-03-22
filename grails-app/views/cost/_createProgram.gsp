<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'cost.program.label')]"/>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'costProgram', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${program}" value="${program.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${program}" value="${program.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${program}" field="code"/>
		<g:input name="order" label="Order" bean="${program}" field="order"/>
	
		<g:if test="${program?.id != null}">
			<input type="hidden" name="id" value="${program.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>