<div class="enum-option-list">
	<div class="float-left">
			<h5>Option List</h5>
		</div>
		<div class="float-right">
			<a id="add-enum-option-link" class="flow-add" href="${createLink(controller:'enumOption', action:'create',params:[enumId:enume.id])}">
			New Option</a>
		</div>
		<div class="admin-table-list">
			<table>
			 <g:if test="${!options.isEmpty()}">
					<tr class="admin-table-header">
						<th>Name</th>
						<th>Description</th>
						<th>Code</th>
						<th>Value</th>
						<th>Order</th>
						<th class="hidden">Manage</th>
					</tr>
					<g:each in="${options}" status="i" var="option"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td><g:i18n field="${option.names}" /></td>
							<td><g:i18n field="${option.descriptions}" /></td>
							<td>${option.code}</td>
							<td>${option.value}</td>
                            <td>${option.order}</td>
						  <td class="hidden">
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="section">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
								<li class="edit-enum-option-link">
							        <g:link controller="enumOption" action="edit" id="${option.id}" class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
									</li>
								<li class="delete-enum-option-link">
							      <g:link controller="enumOption" action="delete" id="${option.id}" class="flow-delete">
									<g:message code="general.text.delete" default="Delete" />
								</g:link>
									</li>
								</ul>
							</div>
							</div> 		
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="6">No Enum Option available 
							<a id="new-enum-option-link" class="flow-add" href="${createLink(controller:'enumOption', action:'create')}">
							New Option</a>
							</td>
						</tr>
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${optionCount}" />
		</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>