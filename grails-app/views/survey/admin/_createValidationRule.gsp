<div id="add-validation-rule" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.validationrule.label',default:'Validation Rule')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container"">
		<div class="data-field-column">
			<g:form url="[controller:'surveyValidationRule', action:'save', params:[targetURI: targetURI]]" useToken="true">
			
				<div class="row ${hasErrors(bean:validation, field:'surveyElement', 'errors')}">
					<label for="surveyElement.id"><g:message code="survey.surveyelement.label" default="Survey Element"/></label>
				    <select id="elements-list" name="surveyElement.id" class="ajax-search-field">
						<g:if test="${validation.surveyElement?.id != null}">
							<option value="${validation.surveyElement.id}" selected>
								<g:i18n field="${validation.surveyElement.dataElement.names}" />[${validation.surveyElement.id}]
							</option>
						</g:if>
					</select>
					<div class="error-list"><g:renderErrors bean="${validation}" field="surveyElement" /></div>
				</div>
			
				<g:input name="prefix" label="${message(code:'survey.validationrule.prefix.label')}" bean="${validation}" field="prefix"/>
		 		<g:i18nRichTextarea name="messages" bean="${validation}" value="${validation.messages}" label="Messages" field="messages" height="150"  width="400" maxHeight="100" />
		 		
				<div class="row ${hasErrors(bean:validation, field:'dependencies', 'errors')}">
					<label><g:message code="survey.validationrule.dependencies.label" default="Dependencies"/>: </label>
				    <select id="dependencies-list" name="dependencies" multiple="true" class="ajax-search-field">
						<g:if test="${validation.dependencies.size() != 0}">
							<g:each in="${validation.dependencies}" var="dependency">
								<option value="${dependency.id}" selected>
									<g:i18n field="${dependency.dataElement.names}" />
									[${dependency.id}]
								</option>
							</g:each>
						</g:if>
					</select>
					<div class="error-list"><g:renderErrors bean="${validation}" field="dependencies" /></div>
				</div>
		
				<div class="row">
					<label><g:message code="survey.validationrule.allowoutlier.label" default="Allow Outlier"/></label>
					<g:checkBox name="allowOutlier" value="${validation.allowOutlier}" />
				</div>
				
				<g:textarea name="expression" label="Expression" bean="${validation}" field="expression" rows="5"/>
				<g:multipleSelect name="groupUuids" label="${message(code:'facility.type.label')}" bean="${validation}" field="groupUuidString" 
					from="${groups}" value="${validation.groupUuids*.toString()}" optionValue="name" optionKey="uuid"/>
			
				<g:if test="${validation.id != null}">
					<input type="hidden" name="id" value="${validation.id}" />
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
				</div>
			</g:form>
		</div>
		
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'surveyElement', action:'getHtmlData']">
				<div class="row">
					<label for="searchText"><g:message code="entity.search.label" default="Search"/>:</label>
			    	<input name="searchText" class="idle-field"/>
			    	<button type="submit"><g:message code="default.button.search.label" default="Search"/></button>
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
		$("#dependencies-list").ajaxChosen({
			type : 'GET',
			dataType: 'json',
			url : "${createLink(controller:'surveyElement', action:'getAjaxData')}"
		}, function (data) {
			var terms = {};
			$.each(data.elements, function (i, val) {
				terms[val.id] = val.surveyElement;
			});
			return terms;
		});
		
		$("#elements-list").ajaxChosen({
			type : 'GET',
			dataType: 'json',
			url : "${createLink(controller:'surveyElement', action:'getAjaxData')}"
		}, function (data) {
			var terms = {};
			$.each(data.elements, function (i, val) {
				terms[val.id] = val.surveyElement;
			});
			return terms;
		});
		
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