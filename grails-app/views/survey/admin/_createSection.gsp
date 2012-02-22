<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.section.label',default:'Section')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'section', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${section}" value="${section?.names}" label="Name" field="names" />
			
		<g:selectFromList name="objective.id" label="${message(code:'survey.objective.label')}" field="objective" optionKey="id" multiple="false"
			from="${objectives}" value="${section.objective?.id}" bean="${section}" values="${objectives.collect {i18n(field:it.names)}}" />
			
		<g:selectFromList name="typeCodes" label="${message(code:'facility.type.label')}" bean="${section}" field="typeCodeString" 
			from="${types}" value="${section.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:input name="order" label="Order" bean="${section}" field="order"/>
		<g:if test="${section.id != null}">
			<input type="hidden" name="id" value="${section.id}"></input>
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
