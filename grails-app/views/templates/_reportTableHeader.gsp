<h4 class='section-title'>
	<g:if test="${controllerName == 'dashboard'}">
		<g:if test="${table == 'program'}">
		<span class='question-default'> <img
			src="${resource(dir:'images/icons',file:'star_small.png')}" />
		</span>
	  <g:i18n field="${currentProgram.names}"/>
	  </g:if>
		<g:elseif test="${table == 'location'}">
			<span class='question-default'> <img
				src="${resource(dir:'images/icons',file:'marker_small.png')}" />
			</span>
		  <g:i18n field="${currentLocation.names}"/>
		</g:elseif>
		<g:else></g:else>
	</g:if>
	<g:elseif test="${controllerName == 'dsr'}">
		<span class='question-default'> <img
			src="${resource(dir:'images/icons',file:'star_small.png')}" />
		</span>
	  <g:i18n field="${currentProgram.names}"/> x <g:i18n field="${currentLocation.names}"/>
	</g:elseif>
	<g:else></g:else>
</h4>
	<g:if test="${controllerName == 'dashboard'}">
		<% def levelUpLinkParams = new HashMap(linkParams) %>
		<g:if test="${table == 'program'}">						
			<% if(currentProgram.parent != null) levelUpLinkParams['program'] = currentProgram.parent.id+"" %>
			<% linkParams = levelUpLinkParams %>
			<a class="level-up" href="${createLink(controller:'dashboard', action:actionName, params:linkParams)}">Level Up</a>	  
	  	</g:if>
		<g:elseif test="${table == 'location'}">
			<% if(currentLocation.parent != null) levelUpLinkParams['location'] = currentLocation.parent?.id+"" %>
			<% linkParams = levelUpLinkParams %>
			<a class="level-up" href="${createLink(controller:'dashboard', action:actionName, params:linkParams)}">Level Up</a>		  
		</g:elseif>
		<g:else></g:else>
	</g:if>