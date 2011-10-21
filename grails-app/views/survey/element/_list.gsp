<!-- Value type question -->

<ul id="element-${surveyElement.id}-${suffix}" class="adv-form element element-list ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>
	
	<g:if test="${print}">
		<g:each in="${0..100}" var="item" status="i">
			<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
				value: null,
				lastValue: null,
				type: type.listType, 
				suffix: suffix+'['+i+']',
				headerSuffix: (headerSuffix==null?suffix:headerSuffix)+'[_]',
				surveyElement: surveyElement,
				enteredValue: enteredValue,
				readonly: readonly
			]"/>
		</g:each>
	</g:if>
	<g:else>
		<g:each in="${value.listValue}" var="item" status="i">
			<li class="element-list-row adv-form-row" data-index="${i}">
				<ul class="adv-form-actions horizontal right">
					<li><a class="element-list-minimize ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.minimize.label" /></a></li>
					<li><a class="element-list-maximize hidden ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.maximize.label" /></a></li>
					<li><a class="element-list-remove ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.removeline.label" /></a></li>
				</ul>
				
				<ul class="minimized-content"></ul>
			
				<input type="hidden" class="list-input" name="surveyElements[${surveyElement.id}].value${suffix}" value="[${i}]"/>
				<input type="hidden" class="list-input-indexes" name="surveyElements[${surveyElement.id}].value${suffix}.indexes" value="[${i}]"/>
				<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
					value: item,
					lastValue: null,
					type: type.listType, 
					suffix: suffix+'['+i+']',
					headerSuffix: (headerSuffix==null?suffix:headerSuffix)+'[_]',
					surveyElement: surveyElement,
					enteredValue: enteredValue,
					readonly: readonly
				]"/>
				<div class="clear"></div>
			</li>
		</g:each>
		<g:if test="${!readonly}">
			<div class="hidden">
				<li class="element-list-row adv-form-row">
					<ul class="adv-form-actions horizontal right">
						<li><a class="element-list-minimize ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.minimize.label" /></a></li>
						<li><a class="element-list-maximize hidden ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.maximize.label" /></a></li>
						<li><a class="element-list-remove ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.removeline.label" /></a></li>
					</ul>
					
					<ul class="minimized-content"></ul>
					
					<input type="hidden" class="list-input" name="surveyElements[${surveyElement.id}].value${suffix}" value="[_]"/>
					<input type="hidden" class="list-input-indexes" name="surveyElements[${surveyElement.id}].value${suffix}.indexes" value="[_]"/>
					<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
						value: null,
						lastValue: null,
						type: type.listType, 
						suffix: suffix+'[_]',
						headerSuffix: (headerSuffix==null?suffix:headerSuffix)+'[_]',
						surveyElement: surveyElement,
						enteredValue: enteredValue,
						readonly: readonly 
					]"/>
					<div class="clear"></div>
				</li>
			</div>
			<a class="element-list-add ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.addline.label" default="Add Line"/></a>
		</g:if>
	</g:else>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>	
	
	<!-- TODO last value -->
</ul>
