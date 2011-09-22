<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Row Option</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
    <div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'tableRow', action:'save']" useToken="true">
				<div class="row">
					<label>Table Name :</label>
					<input type="text" value="${i18n(field: row.question.tableNames)}" class="idle-field" disabled />
				</div>
				
				<input type="hidden" name="question.id"  value="${row.question?.id}" />
				<g:i18nRichTextarea name="names" bean="${row}" value="${row.names}" label="Option" field="names" height="150"  width="300" maxHeight="150" />
				<input type="hidden" name="descriptions.jsonText" value=" "/>
				
				<div class="row ${hasErrors(bean:row, field:'surveyElements', 'errors')}">
					<input type="hidden" name="surveyElement" value="_"/>
			         <g:each in="${row.question.columns}" status="i" var="column">
				         <div class="${(i % 2) == 0 ? 'odd' : 'even'}">
				           <span class="bold">Colunm Name:</span><span> ${i18n(field: column.names)}</span>
				           <div>
					           <label for="dataElement">Data Element:</label>
					           <input type="text" name="dataElement" class="data-element-name idle-field" value="${i18n(field:row.surveyElements[column]?.dataElement?.names)}" />
					           <input type="hidden" name="surveyElement[${column.id}].dataElement.id" class="data-element-id idle-field " value="${row.surveyElements[column]?.dataElement?.id}"/>
					           <input type="hidden" name="surveyElement[${column.id}].id" class="idle-field" value="${row.surveyElements[column]?.id}"/>
					           <input type="hidden" name="surveyElement" value="${column.id}"/>
				           </div>
				         </div>
					 </g:each>
					 <div class="error-list"><g:renderErrors bean="${row}" field="surveyElements" /></div>
				 </div>
					 
			    <div class="clear"></div>
			    <g:input name="order" label="Order" bean="${row}" field="order"/>
		
				<div class="row ${hasErrors(bean:row, field:'groupUuidString', 'errors')}">
					<label for="groups">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
					<div class="error-list">
						<g:renderErrors bean="${row}" field="groupUuidString" />
					</div>
				</div>
				
				<g:if test="${row.id != null}">
					<input type="hidden" name="id" value="${row.id}"></input>
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form">Save Row</button>
					<button id="cancel-button">Cancel</button>
				</div>
			</g:form>
		</div>
		
		<div class="data-search-column">
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
		<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		$('input.data-element-name').bind('focus', function(){
			$('*').removeClass('current-data-element');
			$(this).parents('div:first').addClass('current-data-element');
		});	
		getDataElement(function(event){
			$('div.current-data-element').find('input.data-element-name').val($.trim($(this).text()));
			$('div.current-data-element').find('input.data-element-id').val($(this).data('code'));
		});
		getRichTextContent();	 
	});					
</script>