<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.program.label',default:'Program')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'program', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${program}" value="${program?.names}" label="Name" field="names" />
		<g:input name="order" label="Order" bean="${program}" field="order"/>
		
		<div class="row">
			<input type="hidden" name="survey.id" value="${program.survey.id}" />
			<label><g:message code="survey.label" default="Survey"/>:</label> <g:i18n field="${program.survey.names}"/>
		</div>
		
		<g:selectFromList name="typeCodes" label="${message(code:'facility.type.label')}" bean="${program}" field="typeCodeString" 
			from="${types}" value="${program.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:if test="${program.id != null}">
			<input type="hidden" name="id" value="${program.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
	})					
</script>
