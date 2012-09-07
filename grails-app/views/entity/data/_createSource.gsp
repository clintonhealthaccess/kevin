<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title"><g:message code="source.label"/></h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'source', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${source}" value="${source?.names}" label="${message(code:'entity.name.label')}" field="names" />
		<g:i18nTextarea name="descriptions" bean="${source}" value="${source.descriptions}" label="${message(code:'entity.description.label')}" field="descriptions" height="150"  width="300" maxHeight="150" />
		
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${source}" field="code" />
	
		<g:if test="${source.id != null}">
			<input type="hidden" name="id" value="${source.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
