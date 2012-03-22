<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.section.label')]"/>
		</h3>
		<g:locales />
	</div>

	<g:form url="[controller:'section', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${section}" value="${section?.names}" label="Name" field="names" />
			
		<g:selectFromList name="program.id" label="${message(code:'survey.program.label')}" field="program" optionKey="id" multiple="false"
			from="${programs}" value="${section.program?.id}" bean="${section}" values="${programs.collect {i18n(field:it.names)}}" />
			
		<g:selectFromList name="typeCodes" label="${message(code:'facility.type.label')}" bean="${section}" field="typeCodeString" 
			from="${types}" value="${section.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:input name="order" label="Order" bean="${section}" field="order"/>
		<g:if test="${section.id != null}">
			<input type="hidden" name="id" value="${section.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();	
	})					
</script>
