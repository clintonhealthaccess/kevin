<div class="entity-form-container">
	
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'export.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<div class="data-field-column">
		<g:form url="[controller:'exporter', action:'save', params: [targetURI: targetURI]]" useToken="true">
			
			<div class="row">
				<button type="submit"><g:message code="default.button.save.label"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
			</div>
		</g:form>
	</div>
</div>
