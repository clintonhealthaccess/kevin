<h4 class='section-title'>
	<g:if test="${table == 'program'}">
		<span class='question-default'> <img
			src="${resource(dir:'images/icons',file:'star_small.png')}" />
		</span>
	  <g:i18n field="${currentObjective.names}"/>
  </g:if>
	<g:elseif test="${table == 'location'}">
		<span class='question-default'> <img
			src="${resource(dir:'images/icons',file:'marker_small.png')}" />
		</span>
	  <g:i18n field="${currentOrganisation.names}"/>
	</g:elseif>
	<g:else></g:else>
</h4>