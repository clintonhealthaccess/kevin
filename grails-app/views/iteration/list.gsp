<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'iteration.label', default: 'Iteration')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="entity-list">
    		<div id="iterations" >
	            <h5><g:message code="default.list.label" args="[entityName]" /></h5>
				<div class="float-right">
					<a id="add-iteration-link" class="flow-add" href="${createLink(controller:'iteration', action:'create')}">New Iteration</a>
				</div>
	            <div id="admin-table-list">
	                <table>
	                        <tr class="table-header">
	                        <th><g:message code="general.text.startdate" default="Start Date" /></th>
	                        <th><g:message code="general.text.enddate" default="End Date" /></th>
	                        <th>Action </th>
	                        </tr>
	                    <g:each in="${iterations}" status="i" var="iteration">
	                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
	                            <td class="edit-iteration-link"><g:link action="edit" id="${iteration.id}">${iteration.startDate}</g:link></td>
	                             <td class="edit-iteration-link"><g:link action="edit" id="${iteration.id}">${iteration.endDate}</g:link></td>
	                        	<td class="delete-iteration-link"><g:link action="delete" id="${iteration.id}"><g:message code="general.text.delete" default="Delete" /></g:link></td>
	                        </tr>
	                    </g:each>
	                </table>
	            </div>	
	            <div class="paginateButtons">
	                <g:paginate total="${iterationCount}" />
	            </div>            
	        </div>
			<div class="hidden flow-container"></div>
        </div>
        
        <script type="text/javascript">
			$(document).ready(function() {
					$('#iterations').flow({
						addLinks: '#add-iteration-link, td.edit-iteration-link a',
						deleteLinks: 'td.delete-iteration-link a',
							onSuccess: function(data) {
								if (data.result == 'success') {
									location.reload();
								}
							}
					});				
			});
		</script>
    </body>
</html>
