<!-- Value type question -->

<div class="error-list">
	<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}"/>
</div>	

<ul id="element-${element.id}-${suffix}" class="adv-form element element-list ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>
	
	<g:if test="${print}">
			<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
				location: location,
				value: null,
				lastValue: null,
				type: type.listType, 
				suffix: suffix+'['+i+']',
				headerSuffix: (headerSuffix==null?suffix:headerSuffix)+'[_]',
				element: element,
				validatable: validatable,
				readonly: readonly
			]"/>
	</g:if>
	<g:else>
		<g:each in="${value?.listValue}" var="item" status="i">
			<li class="element-list-row adv-form-row" data-index="${i}">
				<ul class="adv-form-actions horizontal right">
					<li><a class="element-list-minimize ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.minimize.label" /></a></li>
					<li><a class="element-list-maximize hidden ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.maximize.label" /></a></li>
					<li><a class="element-list-remove ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.removeline.label" /></a></li>
				</ul>
				
				<ul class="minimized-content"></ul>
				<input type="hidden" class="js_list-input" name="elements[${element.id}].value${suffix}" value="[${i}]"/>
				<input type="hidden" class="js_list-input-indexes js_always-send" name="elements[${element.id}].value${suffix}.indexes" value="[${i}]"/>
				<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
					location: location,
					value: item,
					lastValue: null,
					type: type.listType, 
					suffix: suffix+'['+i+']',
					headerSuffix: (headerSuffix==null?suffix:headerSuffix)+'[_]',
					element: element,
					validatable: validatable,
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
					<input type="hidden" class="js_list-input" name="elements[${element.id}].value${suffix}" value="[_]"/>
					<input type="hidden" class="js_list-input-indexes js_always-send" name="elements[${element.id}].value${suffix}.indexes" value="[_]"/>
					<g:render template="/survey/element/${type.listType.type.name().toLowerCase()}"  model="[
						location: location,
						value: null,
						lastValue: null,
						type: type.listType, 
						suffix: suffix+'[_]',
						headerSuffix: (headerSuffix==null?suffix:headerSuffix)+'[_]',
						element: element,
						validatable: validatable,
						readonly: readonly 
					]"/>
					<div class="clear"></div>
				</li>
			</div>
			<a class="element-list-add ${!readonly?'loading-disabled':''}" href="#"><g:message code="survey.addline.label"/></a>
		</g:if>
	</g:else>

	<g:if test="${showHints}">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</g:if>

	<!-- TODO last value -->
</ul>
