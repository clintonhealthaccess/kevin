<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.tablequestion.tablecolumn.label')]"/>
		</h3>
		<g:locales />
	</div>
	<div>
	<div id="add-column-col">
	<g:form url="[controller:'tableColumn', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<div class="row">
			<label><g:message code="survey.tablequestion.label"/>:</label>
			<input type="text" value="${i18n(field: column.question.tableNames)}" class="idle-field" disabled />
		</div>
		<input type="hidden" name="question.id"  value="${column.question?.id}" />
		
		<g:i18nRichTextarea name="names" bean="${column}" value="${column.names}" label="${message(code:'survey.tablequestion.tablecolumn.name.label')}" field="names" height="150"  width="300" maxHeight="150" />
		
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${column}" field="order"/>
		
		<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${column}" field="typeCodeString" 
			from="${types}" value="${column.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:if test="${column.id != null}">
			<input type="hidden" name="id" value="${column.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
	</div>
	</div>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();	 
	});					
</script>