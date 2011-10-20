<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="${[message(code:'survey.checkboxoption.label',default:'Checkbox Option')]}"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'checkboxOption', action:'save']" useToken="true">
				<input type="hidden" name="question.id"  value="${option.question?.id}" />
				
				<g:i18nRichTextarea name="names" bean="${option}" value="${option.names}" label="Option" field="names" height="150"  width="300" maxHeight="150" />
				<input type="hidden" name="descriptions.jsonText" value=" "/>
				
				<input type="hidden" name="surveyElement.dataElement.id"  value="${option.surveyElement?.dataElement?.id}" id="data-element-id" />
				<div class="row ${hasErrors(bean:option, field:'surveyElement', 'errors')}">
				    <label for="survey"><g:message code="survey.dataelement.label" default="Data Element"/>:</label> 
				    <input type="text" name="surveyElement.dataElement.name" value="${i18n(field: option.surveyElement?.dataElement?.names)}" id="data-element-name" class="idle-field" disabled />
				    <div class="error-list"><g:renderErrors bean="${option}" field="surveyElement" /></div>
				</div>
	
				<g:input name="order" label="Order" bean="${option}" field="order"/>
				<div class="row ${hasErrors(bean:option, field:'groupUuidString', 'errors')}">
					<label for="groups"><g:message code="general.text.facilitygroups" default="Facility Groups"/>:</label>
					<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
						<g:each in="${groups}" var="group">
							<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
					           ${group.name}
				            </option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${option}" field="groupUuidString" /></div>
				</div>
	
				<g:if test="${option.id != null}">
					<input type="hidden" name="id" value="${option.id}"></input>
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
					<button id="cancel-button"><g:message code="general.text.cancel" default="Cancel"/></button>
				</div>
			</g:form>
		</div>
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'dataElement', action:'getData', params:['include': ['bool']]]">
				<div class="row">
					<label for="searchText"><g:message code="general.text.search" default="Search"/>: </label>
			    	<input name="searchText" class="idle-field"></input>
			    	<button type="submit"><g:message code="default.button.search.label" default="Search"/></button>
					<div class="clear"></div>
				</div>
			</g:form>
		    <ul class="filtered idle-field" id="data" ></ul>
		</div>
		<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getDataElement(function(event) {
			$('#data-element-id').val($(this).data('code'));
			$('#data-element-name').val($.trim($(this).text()));
		});
		getRichTextContent();	 
	});					
</script>