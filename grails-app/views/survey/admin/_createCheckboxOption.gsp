<div id="add-option" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Checkbox Option</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div>
	<div id="add-option-col">
	<g:form url="[controller:'checkboxOption', action:'save']" useToken="true">
	<input type="hidden" name="question.id"  value="${option.question?.id}" />
	<div class="error-list"><g:renderErrors bean="${option}" field="question" /></div>
		<g:i18nRichTextarea name="names" bean="${option}" value="${option.names}" label="Option" field="names" height="150"  width="300" maxHeight="150" />
		<div id="date-element-block">
		<div class="group-list ${hasErrors(bean:option, field:'surveyElement', 'errors')}">
			<input type="hidden" name="surveyElement.dataElement.id"  value="${option.surveyElement?.dataElement?.id}" id="data-element-id" />
		    <div class="row"><label for="survey">Data Element:</label> 
		    <input type="text" name="surveyElement.dataElement.name" value="${i18n(field: option.surveyElement?.dataElement?.names)}" id="data-element-name" class="idle-field" disabled />
		    <div class="error-list"><g:renderErrors bean="${option}" field="surveyElement" /></div>
		    </div>
		 </div>
	    <div class="clear"></div>
	    </div>
		<g:input name="order" label="Order" bean="${option}" field="order"/>
		<div id="orgunitgroup-block">
				<div
					class="group-list ${hasErrors(bean:option, field:'groupUuidString', 'errors')}">
					<label for="groups" class="display-in-block">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
					<div class="error-list">
						<g:renderErrors bean="${option}" field="groupUuidString" />
					</div>
				</div>
			</div>
		<g:if test="${option.id != null}">
			<input type="hidden" name="id" value="${option.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="question-form">Save Option</button>
			&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	</div>
	<div id="data-col">
		<g:form name="search-data-form" class="search-form" url="[controller:'dataElement', action:'getData']">
			<div class="row">
				<label for="searchText">Search: </label>
		    	<input name="searchText" class="idle-field"></input>
		    	<button type="submit">Search</button>
				<div class="clear"></div>
			</div>
		</g:form>
	    <ul class="filtered idle-field" id="data" ></ul>
	</div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getDataElement(
				function(event){
				$('#data-element-id').val($(this).data('code'));
				$('#data-element-name').val($.trim($(this).text()));
		});
		getRichTextContent();	 
	});					
</script>