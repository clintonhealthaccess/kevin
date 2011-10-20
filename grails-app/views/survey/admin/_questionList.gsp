<table>
	<thead>
		<tr>
			<th><g:message code="general.text.question" default="Question"/></th>
			<th><g:message code="general.text.type" default="Type"/></th>
			<th><g:message code="general.text.facilitygroups" default="Facility Groups"/></th>
			<th><g:message code="general.text.order" default="Order"/></th>
			<th><g:message code="general.text.manage" default="Manage"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="question">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><a
					title="Question" href="#" class="cluetip"
					rel="${createLink(controller:question.getType(), action:'getQuestionExplainer', params:[question: question.id])}"
					onclick="return false;"> ${question.getString(i18n(field: question.names).toString(),100)} </a></td>
				<td>${question.getType()}</td>
				<td>${question.groupUuidString}</td>
				<td>${question.order}</td>
				<td>
				<div class="dropdown white-dropdown"> 
				     <a class="selected" href="#" data-type="question"><g:message code="general.text.manage" default="Manage"/></a>
				<div class="hidden dropdown-list">
					<ul>
						<li>
						<g:link class="flow-edit"
							controller="${question.getType()}" action="edit"
							id="${question.id}" class="flow-edit">
							<g:message code="general.text.edit" default="Edit" />
						</g:link>
						
						</li>
						<li class="flow-delete"><g:link
							controller="${question.getType()}" action="delete"
							id="${question.id}" class="flow-delete">
							<g:message code="general.text.delete" default="Delete" />
						</g:link>
						</li>
						<g:if test="${question.getType()=='checkboxQuestion' && question.options.isEmpty()}">
							<li>
								<a class="flow-add" href="${createLink(controller:'checkboxOption', action:'create',params:[questionId: question.id])}">
									<g:message code="survey.addoption.label" default="Add Option"/>
								</a>
							</li>
						</g:if>
					</ul>
				</div>  
				</div>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>

