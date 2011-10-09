<%@ page import="org.chai.kevin.data.Enum" %>
<!-- Value type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-map ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:each in="${type.elementMap}">
		
		<div class="element-map-header">
			<g:i18n field="${surveyElement.headers.get(suffix+'.'+it.key)}"/>
			
			<g:if test="${it.value.type.name().toLowerCase()=='enum' && print && appendix}">
				<g:if test="${it.value.enumCode != null}">
					<g:set var="enume" value="${Enum.findByCode(it.value.enumCode)}"/>
					<div class="text-align-left">--Possible choice--</div>
					<g:each in="${enume?.enumOptions}" var="option">
						<div class="text-align-left"><g:i18n field="${option.names}" /></div>
					</g:each>
				</g:if>
			</g:if>
			
		</div>
		<div class="element-map-body">
		
			<g:render template="/survey/element/${it.value.type.name().toLowerCase()}"  model="[
				value: value?.mapValue?.get(it.key),
				lastValue: lastValue?.mapValue?.get(it.key),
				type: it.value,
				suffix: suffix+'.'+it.key,
				surveyElement: surveyElement,
				enteredValue: enteredValue,
				readonly: readonly
			]"/>
		</div>
	</g:each>
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>	
	
</div>