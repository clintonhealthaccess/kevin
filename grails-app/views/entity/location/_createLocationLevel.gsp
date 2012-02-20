<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'locationLevel.label',default:'Location Level')]"/>
		</h3>
		<g:locales />
	</div>
	
	<g:form url="[controller:'locationLevel', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nTextarea name="names" bean="${locationLevel}" value="${locationLevel?.names}" label="Name" field="names" height="150" width="300" maxHeight="150" />

		<g:input name="code" label="Code" bean="${locationLevel}" field="code"/>
		
		<g:input name="order" label="Order" bean="${locationLevel}" field="order"/>
		
		<g:if test="${locationLevel.id != null}">
			<input type="hidden" name="id" value="${locationLevel.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
	</g:form>
</div>
