<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'expression.label', default: 'Expression')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
    	<div class="entity-list">
    		<div id="expressions">
	            <h5><g:message code="default.list.label" args="[entityName]" /></h5>
	            <g:if test="${flash.message}">
	            <div class="message">${flash.message}</div>
	            </g:if>
	            <div class="float-right">
					<a id="add-expression-link" href="${createLink(controller:'expression', action:'create')}">new expression</a>
				</div>
	            <div class="admin-table-list">
	                <table>
	                        <tr class="admin-table-header">
	                        <th>Names</th>
	                        <th>Descriptions</th>
	                        <th>Manage</th>
	                        </tr>
	                    <g:each in="${expressions}" status="i" var="expression">
	                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
	                            <td class="edit-expression-link"><g:link action="edit" id="${expression.id}"><g:i18n field="${expression.names}"/></g:link></td>
	                        
	                            <td><g:i18n field="${expression.descriptions}"/></td>
	                        
	                        	<td class="delete-expression-link"><a href="${createLink(controller:'expression', action:'delete', id:expression.id)}">delete</a></td>
	                        </tr>
	                    </g:each>
	                </table>
	            </div>
	            <div class="paginateButtons">
	                <g:paginate total="${expressionCount}" max="50" />
	            </div>
			</div>
		
			<div class="hidden flow-container"></div>
		</div>
		
		<script type="text/javascript">
			$(document).ready(function() {
				$('#expressions').flow({
					addLinks: '#add-expression-link, td.edit-expression-link a',
					deleteLinks: 'td.delete-expression-link a',
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
