<g:set var="type" value="${surveyPage.entity.type}"/>

<div id="question-${question.id}" class="question question-table" data-question="${question.id}">
	<h4>
		<span class="question-number">${surveyPage.getQuestionNumber(question)}</span><g:i18n field="${question.names}" />
	</h4>
	
	<g:ifText field="${question.descriptions}">
		<g:render template="/templates/help" model="[content: i18n(field: question.descriptions)]"/>
	</g:ifText>
	
	<div class="question-table-container clear">
		<table class="listing push-20">
			<thead>
				<tr>
					<th class="question-tab-title-name"><g:i18n field="${question.tableNames}" /></th>
				    <g:set var="j" value="${0}"/>
					<g:each in="${surveyPage.getColumns(question)}" var="column">
						<g:set var="j" value="${j++}"/>
						<th class="${surveyPage.getColumns(question).size()!=j?'question-tab-title':''}">
							<g:i18n field="${column.names}" />
						</th>
					</g:each>
				</tr>
			</thead>
			<tbody>
				<g:set var="i" value="${0}"/>
				<g:each in="${surveyPage.getRows(question)}" var="row">
					<g:set var="i" value="${i+1}"/>
					<tr class="${i%2==0?'oddrow':'evenrow'}">
						<td><g:i18n field="${row.names}" /></td>
						<g:each in="${surveyPage.getColumns(question)}" var="column">
							<g:set var="surveyElement" value="${row.surveyElements[column]}"/>
			
							<td id="element-${surveyElement?.id}" class="survey-element">
								<g:if test="${surveyElement != null}">
									<g:set var="dataElement" value="${surveyElement.dataElement}"/>
									<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
				
									<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
										location: enteredValue.entity,
										value: enteredValue.value, 
										lastValue: enteredValue.lastValue,
										type: dataElement.type, 
										suffix:'',
										element: surveyElement, 
										validatable: enteredValue.validatable, 
										readonly: readonly,
										enums: surveyPage.enums
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
