<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'dataLocation.label',default:'Data Location')]"/>
		</h3>
		<g:locales />
	</div>
	
	<g:form url="[controller:'dataLocation', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nTextarea name="names" bean="${location}" value="${location?.names}" label="Name" field="names" height="150" width="300" maxHeight="150" />
		
		<g:input name="code" label="Code" bean="${location}" field="code"/>
		
		<g:selectFromList name="type.id" label="${message(code:'facility.type.label')}" bean="${location}" field="type" 
			from="${types}" value="${location.type?.id}" values="${types.collect{i18n(field:it.names)}}" optionKey="id"/>

		<g:selectFromList name="location.id" label="Location" bean="${location}" field="location" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'location', action:'getAjaxData')}"
			from="${locations}" value="${location.location?.id}" values="${locations.collect{i18n(field:it.names)}}" />

		<g:if test="${location.id != null}">
			<input type="hidden" name="id" value="${location.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
	</g:form>
</div>
