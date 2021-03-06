<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'datalocation.label')]"/>
		</h3>
		<g:locales/>
	</div>
	
	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'dataLocation', action:'save', params:[targetURI: targetURI]]" useToken="true">
				<g:i18nTextarea name="names" bean="${location}" value="${location?.names}" label="${message(code:'entity.name.label')}" field="names" height="150" width="300" maxHeight="150" />
		
				<g:input name="code" label="${message(code:'entity.code.label')}" bean="${location}" field="code"/>
		
				<g:selectFromList name="type.id" label="${message(code:'datalocation.type.label')}" bean="${location}" field="type" 
					from="${types}" value="${location.type?.id}" values="${types.collect{i18n(field:it.names)}}" optionKey="id"/>

				<g:selectFromList name="location.id" label="${message(code:'location.parent.label')}" bean="${location}" field="location" optionKey="id" multiple="false"
					ajaxLink="${createLink(controller:'location', action:'getAjaxData', params: [class: 'Location'])}"
					from="${locations}" value="${location.location?.id}" values="${locations.collect{it.label}}" />

				<g:input name="coordinates" label="${message(code:'location.coordinates.label')}, ${message(code:'location.coordinate.format.label')}" bean="${location}" field="coordinates" value="${location?.coordinates}"/>

				<g:if test="${location.id != null}">
					<input type="hidden" name="id" value="${location.id}"></input>
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
				</div>
			</g:form>
		</div>
		<div class="data-search-column">
			<g:render template="/entity/location/dataLocationDescription" model="[dataLocation: location]"/>
		</div>
	</div>
</div>
