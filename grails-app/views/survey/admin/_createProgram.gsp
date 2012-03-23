<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.program.label')]"/>
		</h3>
		<g:locales />
	</div>

	<g:form url="[controller:'program', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${program}" value="${program?.names}" label="${message(code:'entity.name.label')}" field="names" />
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${program}" field="order"/>
		
		<div class="row">
			<input type="hidden" name="survey.id" value="${program.survey.id}" />
			<label><g:message code="survey.label"/>:</label> <g:i18n field="${program.survey.names}"/>
		</div>
		
		<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${program}" field="typeCodeString" 
			from="${types}" value="${program.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:if test="${program.id != null}">
			<input type="hidden" name="id" value="${program.id}"></input>
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
