<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-table" data-question="${question.id}">
	<h4><span class="question-number">${question.section.getQuestionNumber(question)}</span><g:i18n field="${question.names}" /></h4>
	
	<g:ifText field="${question.descriptions}">
		<p class="show_question_help"><a href="#">Show tips</a></p>
		<div class="question-help-container">
			<p class="question-help"><g:i18n field="${question.descriptions}"/><a class="hide_question_help">Close tips</a></p>
		</div>
	</g:ifText>
	
	<div class="question-table-container clear">
		<table class="listing push-20">
			<thead>
				<tr>
					<th class="question-tab-title-name"><g:i18n field="${question.tableNames}" /></th>
				    <g:set var="j" value="${0}"/>
					<g:each in="${question.getColumns(organisationUnitGroup)}" var="column">
						<g:set var="j" value="${j++}"/>
						<th class="${question.getColumns(organisationUnitGroup).size()!=j?'question-tab-title':''}">
							<g:i18n field="${column.names}" />
						</th>
					</g:each>
				</tr>
			</thead>
			<tbody>
				<g:set var="i" value="${0}"/>
				<g:each in="${question.getRows(organisationUnitGroup)}" var="row">
					<g:set var="i" value="${i+1}"/>
					<tr class="${i%2==0?'oddrow':'evenrow'}">
						<td><g:i18n field="${row.names}" /></td>
						<g:each in="${question.getColumns(organisationUnitGroup)}" var="column">
							<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
			
							<td id="element-${surveyElement?.id}" class="survey-element">
								<g:if test="${surveyElement != null}">
									<g:set var="dataElement" value="${surveyElement.dataElement}"/>
									<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
				
									<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
										value: enteredValue.value, 
										lastValue: enteredValue.lastValue,
										type: dataElement.type, 
										suffix:'',
										surveyElement: surveyElement, 
										enteredValue: enteredValue, 
										readonly: readonly
									]" />
								</g:if>
								<g:else>
									No survey element for this cell.
								</g:else>
							</td>
						</g:each>
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</div>
