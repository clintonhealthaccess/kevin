<%@ page import="org.hisp.dhis.period.Period" %>
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
	            <div class="list">
	                <table>
	                    <thead>
	                        <tr>
	                        <td><g:message code="general.text.startdate" default="Start Date" /></td>
	                        <td><g:message code="general.text.enddate" default="End Date" /></td>
	                        <td>Action </td>
	                        </tr>
	                    </thead>
	                    <tbody>
	                    <g:each in="${iterations}" status="i" var="iteration">
	                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
	                            <td class="edit-iteration-link"><g:link action="edit" id="${iteration.id}">${iteration.startDate}</g:link></td>
	                             <td class="edit-iteration-link"><g:link action="edit" id="${iteration.id}">${iteration.endDate}</g:link></td>
	                        	<td class="delete-iteration-link"><a href="${createLink(controller:'iteration', action:'delete',id:iteration.id)}">Delete</as></td>
	                        </tr>
	                    </g:each>
	                    </tbody>
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
