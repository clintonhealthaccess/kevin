<g:set var="type" value="${surveyPage.entity.type}"/>

<div id="question-${question.id}" class="question question-simple" data-question="${question.id}">
	<h4>
		<span class="question-number">${surveyPage.getQuestionNumber(question)}</span><g:i18n field="${question.names}" />
	</h4>

	<g:ifText field="${question.descriptions}">
		<p class="show-question-help"><a href="#">Show tips</a></p>
		<div class="question-help-container">
			<div class="question-help"><g:i18n field="${question.descriptions}"/><a class="hide-question-help">Close tips</a></div>
		</div>
	</g:ifText>
	<div class="clear"></div>
	
    <g:set var="surveyElement" value="${question.surveyElement}"/>
    
	<g:if test="${print && surveyElement?.dataElement.type.type.name().toLowerCase()=='list' && !appendix}">
		<label>-- <g:message code="survey.print.see.appendix" default="See Appendix"/> --</label>
	</g:if>
	<g:else>
		<div id="element-${surveyElement?.id}" class="survey-element">
			<g:if test="${surveyElement != null}">
				<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
				<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
			
				<g:each in="${question.getValueList(enteredValue.value)}">
					<g:set var="prefix" value="${i.key}"/>
					<g:set var="value" value="${i.value}"/>
					<!-- we output the first part of the question - with the option to add/delete rows -->
					<g:render template="/survey/element/${dataElement.type.getType(question.fixedHeaderPrefix).type.name().toLowerCase()}" model="[
						value: value,
						lastValue: null,
						type: dataElement.type.getType(question.fixedHeaderPrefix),
						suffix: prefix,
						surveyElement: surveyElement,
						enteredValue: enteredValue,
						readonly: readonly,
						print: print,
						appendix: appendix
					]"/>
				</g:each>
				
				<!-- we output each step -->
				<g:each in="${question.steps}" var="step">
					<!-- TODO put this in a template so we can output it anywhere -->
					
					<table>
						<!-- step header -->
						<th>
							
						</th>
					
						<!-- step values -->
						<tr>
							<g:each in="${enteredValue.value.listValue}">
							
								<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}"  model="[
									value: enteredValue.value,
									lastValue: enteredValue.lastValue,
									type: dataElement.type, 
									suffix:'',
									surveyElement: surveyElement, 
									enteredValue: enteredValue, 
									readonly: readonly,
									print: print,
									appendix: appendix
								]"/>
								
							</g:each>
						</tr>
					</table>
					
				</g:each>
			</g:if>
			<g:else>
				No survey element for this question.
			</g:else>
		</div>
	</g:else>
</div>
