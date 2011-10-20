<!-- Value type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-list ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<div class="element-list-header">
		<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
			value: null,
			lastValue: null,
			type: type.listType, 
			suffix: suffix+'[_]',
			surveyElement: surveyElement,
			enteredValue: enteredValue,
			readonly: readonly
		]"/>
		<div class="clear"></div>
	</div>
	
	<div class="element-list-body">
	
		<g:if test="${print}">
			<g:each in="${0..100}" var="item" status="i">
				<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
					value: null,
					lastValue: null,
					type: type.listType, 
					suffix: suffix+'['+i+']',
					surveyElement: surveyElement,
					enteredValue: enteredValue,
					readonly: readonly
				]"/>
			</g:each>
		</g:if>
		
		<g:else>
			<g:each in="${value.listValue}" var="item" status="i">
				<div class="element-list-row" data-index="${i}">
					<input type="hidden" class="list-input" name="surveyElements[${surveyElement.id}].value${suffix}" value="[${i}]"/>
					<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
						value: item,
						lastValue: null,
						type: type.listType, 
						suffix: suffix+'['+i+']',
						surveyElement: surveyElement,
						enteredValue: enteredValue,
						readonly: readonly
					]"/>
					<g:if test="${!readonly}"><span><a class="element-list-remove ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.removeline.label" default="Remove Line"/></a></span></g:if>
					<div class="clear"></div>
				</div>
			</g:each>
			<g:if test="${!readonly}">
				<div class="hidden">
					<div class="element-list-row">
						<input type="hidden" class="list-input" name="surveyElements[${surveyElement.id}].value${suffix}" value="[_]"/>
						<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
							value: null,
							lastValue: null,
							type: type.listType, 
							suffix: suffix+'[_]',
							surveyElement: surveyElement,
							enteredValue: enteredValue,
							readonly: readonly 
						]"/>
						<span><a class="element-list-remove ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.removeline.label" default="Remove Line"/></a></span>
						<div class="clear"></div>
					</div>
				</div>
				<a class="element-list-add ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.addline.label" default="Add Line"/></a>
			</g:if>
		</g:else>
	</div>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>	
	
	<!-- TODO last value -->
</div>
