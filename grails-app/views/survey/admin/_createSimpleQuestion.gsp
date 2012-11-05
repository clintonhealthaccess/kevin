<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.simplequestion.label')]"/>
		</h3>
		<g:locales />
	</div>

	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'simpleQuestion', action:'save', params:[targetURI: targetURI]]" useToken="true">
				<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="${message(code:'survey.question.label')}" field="names" height="100" width="400" maxHeight="150" />
				<g:i18nRichTextarea name="descriptions" bean="${question}" value="${question.descriptions}" label="${message(code:'survey.question.description.label')}" field="descriptions" height="250" width="400" maxHeight="150" />

				<input type="hidden" name="surveyElements[0].dataElement.id" value="${question.surveyElement?.dataElement?.id}" id="data-element-id" />
				<input type="hidden" name="surveyElements[0].id" value="${question.surveyElement?.id}" />
				<div class="row ${hasErrors(bean:question, field:'surveyElement', 'errors')}">
					<label for="data-element-name"><g:message code="dataelement.label"/>:</label>
					<input type="text" name="data-element-name" value="${i18n(field: question.surveyElement?.dataElement?.code)}" id="data-element-name" class="idle-field" disabled />
					<g:if test="${question.surveyElement?.id != null}">
						<span><a href="${createLink(controller:'surveyValidationRule', action:'list', params:['formElement.id': question.surveyElement?.id])}"> <g:message code="default.list.label" args="[message(code:'formelement.validationrule.label')]" /></a> </span>
					</g:if>
					<div class="error-list">
						<g:renderErrors bean="${question}" field="surveyElement" />
					</div>
				</div>

				<g:if test="${headerPrefixes != null && !headerPrefixes.empty}">
					<div class="row ${hasErrors(bean:question, field:'surveyElement.headers', 'errors')}">
						<a href="#" onclick="$(this).next().toggle();return false;"><g:message code="survey.simplequestion.headers.label"/>:</a> 
						<div class="hidden">
							<g:each in="${headerPrefixes}" var="headerPrefix">
								<input type="hidden" name="headerList" value="${headerPrefix}"/>
								<g:i18nRichTextarea name="headerList[${headerPrefix}]" bean="${question}" value="${question.surveyElement.headers[headerPrefix]}" label="${headerPrefix}" field="surveyElement.headers" height="50"/>
							</g:each>
						</div>
					</div>
				</g:if>

				<g:input name="order" label="${message(code:'entity.order.label')}" bean="${question}" field="order" />

				<g:input name="code" label="${message(code:'entity.code.label')}" bean="${question}" field="code" />

				<g:selectFromList name="section.id" label="${message(code:'survey.section.label')}" field="section" optionKey="id" multiple="false"
					from="${sections}" value="${question.section?.id}" bean="${question}" values="${sections.collect {i18n(field:it.names)}}" />
			
				<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${question}" field="typeCodeString" 
					from="${types}" value="${question.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

				<g:if test="${question.id != null}">
					<input type="hidden" name="id" value="${question.id}"></input>
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
				</div>
			</g:form>
		</div>
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'data', action:'getData', params:[class: 'RawDataElement']]">
				<div class="row">
					<label for="searchText"><g:message code="entity.search.label"/>: </label> <input name="searchText" class="idle-field"></input>
					<button type="submit"><g:message code="default.button.search.label"/></button>
					<div class="clear"></div>
				</div>
			</g:form>
			<ul class="filtered idle-field" id="data"></ul>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		getDataElement(function(event){
			$('#data-element-id').val($(this).data('code'));
			$('#data-element-name').val($.trim($(this).text()));
		});
	});					
</script>