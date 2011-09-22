<!-- Value type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-list ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:set var="listType" value="${type.listType}"/>
	<div class="element-list-header">
		<g:render template="/survey/element/${listType.type.name().toLowerCase()}"  model="[
			value: null,
			lastValue: null,
			type: listType, 
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
				<g:render template="/survey/element/${listType.type.name().toLowerCase()}"  model="[
					value: null,
					lastValue: null,
					type: listType, 
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
					<input type="hidden" name="surveyElements[${surveyElement.id}].value${suffix}" value="[${i}]"/>
					<g:render template="/survey/element/${listType.type.name().toLowerCase()}"  model="[
						value: item,
						lastValue: null,
						type: listType, 
						suffix: suffix+'['+i+']',
						surveyElement: surveyElement,
						enteredValue: enteredValue,
						readonly: readonly
					]"/>
					<span><a class="element-list-remove" href="#">remove line</a></span>
					<div class="clear"></div>
				</div>
			</g:each>
			<div class="hidden">
				<div class="element-list-row">
					<input type="hidden" name="surveyElements[${surveyElement.id}].value${suffix}" value="[_]"/>
					<g:render template="/survey/element/${listType.type.name().toLowerCase()}"  model="[
						value: null,
						lastValue: null,
						type: listType, 
						suffix: suffix+'[_]',
						surveyElement: surveyElement,
						enteredValue: enteredValue,
						readonly: readonly 
					]"/>
					<span><a class="element-list-remove" href="#">remove line</a></span>
					<div class="clear"></div>
				</div>
			</div>
			<a class="element-list-add" href="#">add line</a>
		</g:else>
	</div>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>	
	
	<!-- TODO last value -->
</div>
