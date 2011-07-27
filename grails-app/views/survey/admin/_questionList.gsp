<div class="survey-admin-entity-list">
	<div id="questions">
		<div class="float-left">
			<h5>Question List</h5>
		</div>
		<div class="float-right">
			<div class="filter">
				<div class="dropdown">
					<a class="selected" href="#" data-type="question">New Question</a>
					<div class="hidden dropdown-list">
						<ul>
							<li><a id="add-simplequestion-link" class="flow-add"
								href="${createLink(controller:'simpleQuestion', action:'create', params:[sectionId: section.id])}">New
									Simple Question</a>
							</li>
							<li><a id="add-checkboxquestion-link" class="flow-add"
								href="${createLink(controller:'checkboxQuestion', action:'create', params:[sectionId: section.id])}">New
									Checkbox Question</a>
							</li>
							<li><a id="add-tablequestion-link" class="flow-add"
								href="${createLink(controller:'tableQuestion', action:'create', params:[sectionId: section.id])}">New
									Table Question</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<div id="admin-table-list">
			<table>
				<g:if test="${!questions.isEmpty()}">
					<tr class="table-header">
						<th>Question</th>
						<th>Type</th>
						<th>Organisation Unit Groups</th>
						<th>Order</th>
						<th>Manage</th>
					</tr>
					<g:each in="${questions}" status="i" var="question">
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<td><a class="question-explainer display-in-block"
								title="Question" href="#"
								rel="${createLink(controller:question.getType(), action:'getQuestionExplainer', params:[question: question.id])}"
								onclick="return false;"> ${question.getString(i18n(field: question.names).toString(),100)} </a></td>
							<td>${question.getType()}</td>
							<td>${question.groupUuidString}</td>
							<td>${question.order}</td>
							<td>
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="question">Manage</a>
							<div class="hidden dropdown-list">
								<ul>
									<li class="edit-question-link">
									<g:link
									controller="${question.getType()}" action="edit"
									id="${question.id}" class="flow-edit">
									<g:message code="general.text.edit" default="Edit" />
								</g:link>
									
									</li>
									<li class="delete-question-link"><g:link
									controller="${question.getType()}" action="delete"
									id="${question.id}" class="flow-delete">
									<g:message code="general.text.delete" default="Delete" />
								</g:link>
									</li>
									<g:if
									test="${question.getType()=='checkboxQuestion' && question.options.isEmpty()}">
									<li>
									<a id="new-option-link" class="flow-add"
										href="${createLink(controller:'checkboxOption', action:'create',params:[questionId: question.id])}">Add Option</a></li>
								</g:if>
								</ul>
							</div>  
							</div>
							</td>
						</tr>
					</g:each>
				</g:if>
				<g:else>
					<tr>
						<td colspan="5">
							<div class="float-left">
								<div class="filter">
									<div class="dropdown">
										No question available <a class="selected" href="#"
											data-type="question">Add Question</a>
										<div class="hidden dropdown-list">
											<ul>
												<li><a id="new-simplequestion-link" class="flow-add"
													href="${createLink(controller:'simpleQuestion', action:'create', params:[sectionId: section.id])}">New
														Simple Question</a></li>
												<li><a id="new-checkboxquestion-link" class="flow-add"
													href="${createLink(controller:'checkboxQuestion', action:'create', params:[sectionId: section.id])}">New
														Checkbox Question</a></li>
												<li><a id="new-tablequestion-link" class="flow-add"
													href="${createLink(controller:'tableQuestion', action:'create', params:[sectionId: section.id])}">New
														Table Question</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</td>
					</tr>
				</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${questionCount}" />
		</div>
		<div class="hidden flow-container"></div>
	</div>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		$('a.question-explainer').cluetip(cluetipOptions);
		
		$('#question').flow({
			addLinks: '#new-option-link',
			onSuccess: function(data) {
				if (data.result == 'success') {}
				}
			});	
	});
</script>
