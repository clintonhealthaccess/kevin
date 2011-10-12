<%@ page import="org.chai.kevin.data.Enum" %>

<% if (levels == null) levels = new java.util.Stack() %>
<% levels.push(type) %>

<!-- Value type question -->
<ul id="element-${surveyElement.id}-${suffix}" class="element element-map-level-${levels.size()} element-map ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:each in="${type.elementMap}">
		<li>
		  	<div class="element-map-header">
		  		<g:if test="${!it.value.isComplexType()}">
	  				<label><g:i18n field="${surveyElement.headers.get(suffix+'.'+it.key)}"/></label>
	  			</g:if>
	  			<g:else>
	  				<g:if test="${levels.size() == 1}">
	  					<h5><g:i18n field="${surveyElement.headers.get(suffix+'.'+it.key)}"/></h5>
	  				</g:if>
	  				<g:else>
	  					<h6><g:i18n field="${surveyElement.headers.get(suffix+'.'+it.key)}"/></h6>
	  				</g:else>
	  			</g:else>
				
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
	  	</li>
	</g:each>
	
	<% levels.pop() %>
	
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>	
	
</ul>