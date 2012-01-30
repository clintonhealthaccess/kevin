<% if (levels == null) levels = new java.util.Stack() %>

<g:if test="${showHints}">
	<div class="admin-hint">Element: ${element.id} - Prefix: ${suffix}</div>
</g:if>

<!-- Value type question -->
<ul id="element-${element.id}-${suffix}" class="horizontal element element-map-level-${levels.size()} element-map ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>
	
	<g:if test="${levels.size() == 0}">
		<h5 class="adv-form-title">Basic Identifiers</h5>
	</g:if>	
	
	<% levels.push(type) %>
	<g:each in="${type.elementMap}" var="it" status="i">
	
		<li class="${(it.value.isComplexType() && levels.size() == 1 && it.value.getAttribute('block')!='true')?'adv-form-section':''} ${it.value.isComplexType() && (levels.size() > 1 || it.value.getAttribute('block')=='true')?'adv-form-subsection':''} ${type.elementMap.size() == i+1?'last':''}">
	  		<g:if test="${!it.value.isComplexType()}">
  				<label><g:i18n field="${element.headers.get(headerSuffix+'.'+it.key)}"/></label>
  			</g:if>
  			<g:else>
  				<g:if test="${levels.size() == 1 && it.value.getAttribute('block')!='true'}">
  					<h5><g:i18n field="${element.headers.get(headerSuffix+'.'+it.key)}"/></h5>
  				</g:if>
  				<g:if test="${levels.size() > 1 || it.value.getAttribute('block')=='true'}">
  					<h6><g:i18n field="${element.headers.get(headerSuffix+'.'+it.key)}"/></h6>
  				</g:if>
  			</g:else>
						
  			<g:render template="/survey/element/${it.value.type.name().toLowerCase()}"  model="[
  				location: location,
  				value: value?.mapValue?.get(it.key),
  				lastValue: lastValue?.mapValue?.get(it.key),
  				type: it.value,
  				suffix: suffix+'.'+it.key,
  				headerSuffix:  (headerSuffix==null?suffix:headerSuffix)+'.'+it.key,
  				element: element,
  				validatable: validatable,
  				readonly: readonly
  			]"/>
	  	</li>
	</g:each>
	<% levels.pop() %>
	
	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>	
	
</ul>
