<%@ page import="org.chai.kevin.data.Enum" %>
<!-- Value type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-map ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:set var="mapValue" value="${value?.mapValue}"/>
	<g:set var="lastMapValue" value="${lastValue?.mapValue}"/>
	
	<g:each in="${type.elementMap}" var="entry" status="i">
		<g:set var="key" value="${entry.key}"/>
		<g:set var="keyType" value="${entry.value}"/>
		
		<div class="element-map-header">
			<g:i18n field="${surveyElement.headers.get(suffix+'.'+key)}"/>
			
			<g:if test="${keyType.type.name().toLowerCase()=='enum' && print && appendix}">
				<g:if test="${keyType.enumCode != null}">
					<g:set var="enume" value="${Enum.findByCode(keyType.enumCode)}"/>
					<span class="display-in-block text-align-left">--Possible choice--</span>
					<g:each in="${enume?.enumOptions}" var="option">
						<span class="display-in-block text-align-left"><g:i18n field="${option.names}" /></span>
					</g:each>
				</g:if>
			</g:if>
			
		</div>
		<div class="element-map-body">
			<g:render template="/survey/element/${keyType.type.name().toLowerCase()}"  model="[
				value: mapValue?.get(key),
				lastValue: lastMapValue?.get(key),
				type: keyType,
				suffix: suffix+'.'+key,
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