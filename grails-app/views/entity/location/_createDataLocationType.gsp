<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'datalocationtype.label')]"/>
		</h3>
		<g:locales/>
	</div>
	
	<g:form url="[controller:'dataLocationType', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nTextarea name="names" bean="${dataLocationType}" value="${dataLocationType?.names}" label="${message(code:'entity.name.label')}" field="names" height="150" width="300" maxHeight="150" />
		
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${dataLocationType}" field="code"/>
		
		<div class="row">
			<label><g:message code="datalocationtype.defaultselected.label"/></label>
			<g:checkBox name="defaultSelected" value="${dataLocationType.defaultSelected}" />
		</div>
		
		<g:if test="${dataLocationType.id != null}">
			<input type="hidden" name="id" value="${dataLocationType.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
</div>
