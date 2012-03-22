<div id="add-validation-rule" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.validationrule.label')]"/>
		</h3>
		<g:locales />
	</div>
	<div class="forms-container"">
		<div class="data-field-column">
			<g:form url="[controller:'surveyValidationRule', action:'save', params:[targetURI: targetURI]]" useToken="true">

				<g:selectFromList name="surveyElement.id" label="${message(code:'survey.surveyelement.label')}" field="surveyElement" optionKey="id" multiple="false"
					ajaxLink="${createLink(controller:'surveyElement', action:'getAjaxData')}" from="${surveyElements}"
					value="${validation.surveyElement?.id}" bean="${validation}"
					values="${surveyElements.collect {i18n(field:it.dataElement.names)+' - '+i18n(field:it.surveyQuestion?.section?.names)+' - '+i18n(field:it.survey?.names)+'['+it.id+']'}}" />
			
				<g:input name="prefix" label="${message(code:'survey.validationrule.prefix.label')}" bean="${validation}" field="prefix"/>
		 		<g:i18nRichTextarea name="messages" bean="${validation}" value="${validation.messages}" label="Messages" field="messages" height="150"  width="400" maxHeight="100" />
		 		
		 		<g:selectFromList name="dependencies" label="${message(code:'survey.validationrule.dependencies.label')}" field="dependencies" optionKey="id" multiple="true"
					ajaxLink="${createLink(controller:'surveyElement', action:'getAjaxData')}" from="${dependencies}" 
					value="${validation.dependencies*.id}" bean="${validation}"
					values="${dependencies.collect {i18n(field:it.dataElement.names)+' - '+i18n(field:it.surveyQuestion?.section?.names)+' - '+i18n(field:it.survey?.names)+'['+it.id+']'}}" />
			
				<div class="row">
					<label><g:message code="survey.validationrule.allowoutlier.label"/></label>
					<g:checkBox name="allowOutlier" value="${validation.allowOutlier}" />
				</div>
				
				<g:textarea name="expression" label="Expression" bean="${validation}" field="expression" value="${validation.expression}" rows="5"/>
				
				<g:selectFromList name="typeCodes" label="${message(code:'facility.type.label')}" bean="${validation}" field="typeCodeString" 
					from="${types}" value="${validation.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>
			
				<g:if test="${validation.id != null}">
					<input type="hidden" name="id" value="${validation.id}" />
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
				</div>
			</g:form>
		</div>
		
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'surveyElement', action:'getHtmlData']">
				<div class="row">
					<label for="searchText"><g:message code="entity.search.label"/>:</label>
			    	<input name="searchText" class="idle-field"/>
			    	<button type="submit"><g:message code="default.button.search.label"/></button>
					<div class="clear"></div>
				</div>
			</g:form>
		    <ul class="filtered idle-field" id="data" ></ul>
		</div>
	</div>
	<div class="clear"></div>
</div>

<script type="text/javascript">
	$(document).ready(function() {		
		getDataElement(function(event){
			if ($('.in-edition').size() == 1) {
				var edition = $('.in-edition')[0]
				$(edition).replaceSelection('$'+$(this).data('code'));
			}
		});
		$('#add-validation-rule textarea')
		.bind('click keypress focus',
			function(){
				$(this).addClass('in-edition');
			}
		)
		.bind('blur',
			function(){
				$(this).removeClass('in-edition');
			}
		);
			 
		getRichTextContent();
	});					
</script>