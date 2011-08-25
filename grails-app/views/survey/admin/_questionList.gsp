<table>
	<thead>
		<tr>
			<th>Question</th>
			<th>Type</th>
			<th>Organisation Unit Groups</th>
			<th>Order</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="question">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td><a class="question-explainer display-in-block"
					title="Question" href="#"
					rel="${createLink(controller:question.getType(), action:'getQuestionExplainer', params:[question: question.id])}"
					onclick="return false;"> ${question.getString(i18n(field: question.names).toString(),100)} </a></td>
				<td>${question.getType()}</td>
				<td>${question.groupUuidString}</td>
				<td>${question.order}</td>
				<td>
				<div class="dropdown white-dropdown"> 
				     <a class="selected" href="#" data-type="question">Manage</a>
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
								<a class="flow-add" href="${createLink(controller:'checkboxOption', action:'create',params:[questionId: question.id])}">Add Option</a>
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

<script type="text/javascript">
	$(document).ready(function() {
		$('a.question-explainer').cluetip(cluetipOptions);
		
	});
</script>
