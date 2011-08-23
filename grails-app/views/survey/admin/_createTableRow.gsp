<div id="add-row" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Row Option</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div>
	<div id="add-row-col">
	<g:form url="[controller:'tableRow', action:'save']" useToken="true">
	<label class="display-in-block">Table Name :</label>
	<input type="text" value="${i18n(field: row.question.tableNames)}" class="idle-field" disabled />
	<input type="hidden" name="question.id"  value="${row.question?.id}" />
	<div class="error-list"><g:renderErrors bean="${row}" field="question" /></div>
		<g:i18nRichTextarea name="names" bean="${row}" value="${row.names}" label="Option" field="names" height="150"  width="300" maxHeight="150" />
		<div id="date-element-block">
			<div class="group-list ${hasErrors(bean:row, field:'surveyElements', 'errors')}">
		         <g:each in="${row.question.columns}" status="i" var="column">
			         <div class="survey-table-row ${(i % 2) == 0 ? 'odd' : 'even'}">
			           <label>Colunm Name:</label><span> ${i18n(field: column.names)}</span>
			           <span class="display-in-block">
				           <label class="display-in-block">Data Element:</label>
				           <input type="text" name="" class="data-element-name idle-field" value="${i18n(field:row.surveyElements[column]?.dataElement?.names)}" />
				           <input type="hidden" name="surveyElement[${column.id}].dataElement.id" class="data-element-id idle-field " value="${row.surveyElements[column]?.dataElement?.id}"/>
				           <input type="hidden" name="surveyElement[${column.id}].id" class="survey-element-id idle-field" value="${row.surveyElements[column]?.id}"/>
				           <input type="hidden" name="column[${column.id}].id" class="column-id idle-field" value="${column?.id}"/>
			           </span>
			         </div>
				 </g:each>
			 </div>
			 <div class="error-list"><g:renderErrors bean="${row}" field="surveyElements" /></div>
	    <div class="clear"></div>
	    </div>
	    <g:input name="order" label="Order" bean="${row}" field="order"/>
		<div id="orgunitgroup-block">
				<div class="group-list ${hasErrors(bean:row, field:'groupUuidString', 'errors')}">
					<label for="groups" class="display-in-block">Organisation Unit Group:</label>
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
			</div>
		<g:if test="${row.id != null}">
			<input type="hidden" name="id" value="${row.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Option</button>
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
		$('input.data-element-name').bind('focus', function(){
			$('*').removeClass('current-data-element');
			$(this).parents('div:first').addClass('current-data-element');
		});	
		getDataElement(
			function(event){
				$('div.current-data-element').find('input.data-element-name').val($.trim($(this).text()));
				$('div.current-data-element').find('input.data-element-id').val($(this).data('code'));
			});	
		getRichTextContent();	 
	});					
</script>