<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<g:sortableColumn property="code" title="${message(code: 'enum.code.label', default: 'Code')}" />
			<th>Number of Option</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="enumation"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><g:i18n field="${enumation.names}" /></td>
				<td><g:i18n field="${enumation.descriptions}" /></td>
				<td>${enumation.code}</td>
				<td>${enumation.enumOptions.size()}</td>
				<td>
				<div class="dropdown white-dropdown"> 
					<a class="selected" href="#" data-type="section">Manage</a>
					<div class="hidden dropdown-list">
						<ul>
							<li class="add-enum-link">
								<a href="${createLink(controller:'enumOption', action:'list',params:[enumId: enumation.id])}">
									<g:message code="general.text.options" default="Options" />
								</a>
							</li>
							<div class="hidden">
							<li>
								<g:link controller="enum" action="edit" id="${enumation.id}" class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
							</li>
							<li>
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
	</tbody>
</table>
