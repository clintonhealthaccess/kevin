<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.objective.label',default:'Objective')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'objective', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${objective}" value="${objective?.names}" label="Name" field="names" />
	   	<g:i18nRichTextarea name="descriptions" bean="${objective}" value="${objective?.descriptions}" label="Descriptions" field="descriptions" height="100"  width="300" maxHeight="100" />
		
		<div class="row">
			<input type="hidden" name="survey.id" value="${objective.survey.id}" />
			<label><g:message code="survey.label" default="Survey"/>:</label> <g:i18n field="${objective.survey.names}"/>
		</div>
		
		<g:multipleSelect name="groupUuids" label="${message(code:'facility.type.label')}" bean="${objective}" field="groupUuidString" 
			from="${groups}" value="${objective.groupUuids*.toString()}" optionValue="name" optionKey="uuid"/>

		<g:input name="order" label="Order" bean="${objective}" field="order"/>
		<g:if test="${objective.id != null}">
			<input type="hidden" name="id" value="${objective.id}"></input>
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
