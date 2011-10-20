<%@ page import="org.chai.kevin.data.Enum" %>

<% if (levels == null) levels = new java.util.Stack() %>

<!-- Value type question -->
<ul id="element-${surveyElement.id}-${suffix}" class="horizontal element element-map-level-${levels.size()} element-map ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>
	
	<g:if test="${levels.size() == 0}">
		<h5 class="adv-form-title">General Information</h5>
	</g:if>	
	
	<% levels.push(type) %>
	<g:each in="${type.elementMap}" var="it" status="i">
	
		<li class="${(it.value.isComplexType() && levels.size() == 1 && it.value.getAttribute('block')!='true')?'adv-form-section':''} ${it.value.isComplexType() && (levels.size() > 1 || it.value.getAttribute('block')=='true')?'adv-form-subsection':''} ${type.elementMap.size() == i+1?'last':''}">
	  		<g:if test="${!it.value.isComplexType()}">
  				<label><g:i18n field="${surveyElement.headers.get(headerSuffix+'.'+it.key)}"/></label>
  			</g:if>
  			<g:else>
  				<g:if test="${levels.size() == 1 && it.value.getAttribute('block')!='true'}">
  					<h5><g:i18n field="${surveyElement.headers.get(headerSuffix+'.'+it.key)}"/></h5>
  				</g:if>
  				<g:if test="${levels.size() > 1 || it.value.getAttribute('block')=='true'}">
  					<h6><g:i18n field="${surveyElement.headers.get(headerSuffix+'.'+it.key)}"/></h6>
  				</g:if>
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
			
  			<g:render template="/survey/element/${it.value.type.name().toLowerCase()}"  model="[
  				value: value?.mapValue?.get(it.key),
  				lastValue: lastValue?.mapValue?.get(it.key),
  				type: it.value,
  				suffix: suffix+'.'+it.key,
  				headerSuffix:  (headerSuffix==null?suffix:headerSuffix)+'.'+it.key,
  				surveyElement: surveyElement,
  				enteredValue: enteredValue,
  				readonly: readonly
  			]"/>
	  	</li>
	</g:each>
	<% levels.pop() %>
	
	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>	
	
</ul>