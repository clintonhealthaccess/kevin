<div id="table-preview" class="admin-table-list white-box">
	<g:i18n field="${question.names}" />
	<table class="question-table" id="question-table-${question.id}">
	<tr class="admin-table-header">
	<th><g:i18n field="${question.tableNames}" /></th>
	    <g:set var="j" value="${0}"/>
		<g:each in="${question.getColumns()}" var="column">
		<g:set var="j" value="${j++}"/>
			<th class="${question.getColumns().size()!=j?'question-tab-title':''}">
			<g:i18n field="${column.names}" />			
		  <span class="display-in-block"> 
			  <g:link controller="tableColumn" action="edit" id="${column.id}" class="flow-edit"> 
			     <g:message code="general.text.edit" default="Edit" />
			  </g:link>&nbsp;
			   
			  <g:link controller="tableColumn" action="delete" id="${column.id}" class="flow-delete">
			     <g:message code="general.text.delete" default="Delete" />
			     </g:link> 
			</span>
			
			</th>
		</g:each>
	</tr>
	<g:set var="i" value="${0}"/>
	<g:each in="${question.getRows()}" var="row">
	<g:set var="i" value="${i+1}"/>
		<tr class="${i%2==0?'odd':'even'}">
		<td>
		<g:i18n field="${row.names}" />
			
			<span class="display-in-block"> 
			  <g:link controller="tableRow" action="edit" id="${row.id}" class="flow-edit"> 
			     <g:message code="general.text.edit" default="Edit" />
			  </g:link>&nbsp;
			   
			  <g:link controller="tableRow" action="delete" id="${row.id}" class="flow-delete">
			     <g:message code="general.text.delete" default="Delete" />
			     </g:link> 
			</span>
			
		</td>
		<g:each in="${question.getColumns()}" var="column">
			<g:set var="surveyElement" value="${row?.surveyElements[column]}"/>
			<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
			<td>
				dataElement
			</td>
		</g:each>
		</tr>
	</g:each>
	</table>
	<button id="cancel-button">Done</button>
</div>

<div class="hidden flow-container"></div>

<script type="text/javascript">
	$('#table-preview').flow({
		addLinks : ['.flow-edit'],
		onSuccess : function(data) {}
	});
</script>