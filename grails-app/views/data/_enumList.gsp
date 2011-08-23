<div class="enum-list">
	<div class="float-left">
			<h5>Enum List</h5>
		</div>
		<div class="float-right">
			<a id="add-enum-link" class="flow-add" href="${createLink(controller:'enum', action:'create')}">
			New Enum</a>
		</div>
		<div class="admin-table-list">
			<table>
			 <g:if test="${!enums.isEmpty()}">
					<tr class="admin-table-header">
					    <g:sortableColumn property="id" title="${message(code: 'enum.id.label', default: 'Id')}" />
						<th>Name</th>
						<th>Description</th>
						<g:sortableColumn property="code" title="${message(code: 'enum.code.label', default: 'Code')}" />
						<th>Number of Option</th>
						<th>Manage</th>
					</tr>
					<g:each in="${enums}" status="i" var="enumation"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
						   <td>${enumation.id}</td> 
							<td><g:i18n field="${enumation.names}" /></td>
							<td><g:i18n field="${enumation.descriptions}" /></td>
							<td>${enumation.code}</td>
							<td>${enumation.enumOptions.size()}</td>
						  <td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="section">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
								<li class="add-enum-link">
									<a  class="enum-option-list" href="${createLink(controller:'enumOption', action:'list',params:[enumId: enumation.id])}">
									<g:message code="general.text.options" default="Options" />
									</a>
									</li>
									<div class="hidden">
								<li class="edit-enum-link">
							        <g:link controller="enum" action="edit" id="${enumation.id}" class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
									</li>
								<li class="delete-enum-link">
							      <g:link controller="enum" action="delete" id="${enumation.id}" class="flow-delete">
									<g:message code="general.text.delete" default="Delete" />
								</g:link>
									</li>
									</div>
								</ul>
							</div>
							</div> 		
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="6">No Enum available 
							<a id="new-enum-link" class="flow-add" href="${createLink(controller:'enum', action:'create')}">
							New Enum</a>
							</td>
						</tr>
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${enumCount}" />
		</div>
	<div class="clear"></div>
</div>